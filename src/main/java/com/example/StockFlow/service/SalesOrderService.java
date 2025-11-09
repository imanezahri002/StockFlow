package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.SalesOrderRequest;
import com.example.StockFlow.dto.response.SalesOrderResponse;
import com.example.StockFlow.entity.*;
import com.example.StockFlow.entity.enums.OrderStatus;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.SalesOrderMapper;
import com.example.StockFlow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesOrderService {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementService inventoryMovementService;
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderMapper salesOrderMapper;

    @Transactional
    public SalesOrderResponse createSalesOrder(SalesOrderRequest request, String token) {
        // 1️⃣ Récupérer l'utilisateur connecté
        var userResponse = authService.getCurrentUser(token).getUser();
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new CustomException("Utilisateur introuvable"));

        // 2️⃣ Vérifier le rôle client
        if (!user.getRole().name().equalsIgnoreCase("CLIENT")) {
            throw new CustomException("Seuls les clients peuvent créer une commande !");
        }

        // 3️⃣ Créer la commande
        SalesOrder order = SalesOrder.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        // 4️⃣ Créer les lignes de commande
        List<SalesOrderLine> lines = request.getOrderLines().stream().map(lineRequest -> {
            Product product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new CustomException("Produit non trouvé"));

            BigDecimal unitPrice = product.getOriginalPrice().add(product.getProfit());
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(lineRequest.getQuantity()));

            return SalesOrderLine.builder()
                    .salesOrder(order)
                    .product(product)
                    .quantity(lineRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .backorder(false)
                    .build();
        }).toList();

        order.setOrderLines(lines);

        // 5️⃣ Vérifier et réserver le stock
        boolean allReserved = reserveStock(order);

        if (allReserved) {
            order.setStatus(OrderStatus.RESERVED);
        } else {
            order.setStatus(OrderStatus.CREATED);
            throw new CustomException("Stock insuffisant pour un ou plusieurs produits.");
        }

        // 6️⃣ Sauvegarder la commande
        salesOrderRepository.save(order);

        // 7️⃣ Retourner la réponse
        return salesOrderMapper.toResponse(order);
    }

    /**
     * Réserve le stock dans les entrepôts et crée les mouvements OUTBOUND.
     */
    private boolean reserveStock(SalesOrder order) {
        for (SalesOrderLine line : order.getOrderLines()) {
            int quantityToReserve = line.getQuantity();
            List<Warehouse> warehouses = warehouseRepository.findAll();

            for (Warehouse warehouse : warehouses) {
                Optional<Inventory> optionalInventory = inventoryRepository.findByWarehouseAndProduct(warehouse, line.getProduct());
                if (optionalInventory.isEmpty()) continue;

                Inventory inventory = optionalInventory.get();
                int available = inventory.getQtyOnHand();

                if (available <= 0) continue;

                // Calcul de la quantité à réserver
                int reservedQty = Math.min(available, quantityToReserve);

                // 🧮 Mise à jour du stock
                inventory.setQtyOnHand(available - reservedQty);
                inventoryRepository.save(inventory);

                // 🔹 Créer un mouvement OUTBOUND
                String reference = "Order-" + (order.getId() != null ? order.getId() : "temp");
                String desc = "Réservation du produit '" + line.getProduct().getName() +
                        "' depuis l'entrepôt '" + warehouse.getName() + "'";
                inventoryMovementService.createOutboundMovement(inventory, reservedQty, reference, desc);

                // Mise à jour de la quantité restante à réserver
                quantityToReserve -= reservedQty;

                if (quantityToReserve == 0) break; // produit complètement réservé
            }

            // Si après tous les entrepôts, la quantité reste insuffisante
            if (quantityToReserve > 0) {
                return false;
            }
        }
        return true;
    }
}
