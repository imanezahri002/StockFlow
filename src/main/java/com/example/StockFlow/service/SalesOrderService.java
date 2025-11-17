// java
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
        //  Récupérer l'utilisateur connecté
        var userResponse = authService.getCurrentUser(token).getUser();
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new CustomException("Utilisateur introuvable"));

        //  Vérifier le rôle client
        if (!user.getRole().name().equalsIgnoreCase("CLIENT")) {
            throw new CustomException("Seuls les clients peuvent créer une commande !");
        }

        //  Créer la commande
        SalesOrder order = SalesOrder.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        //  Créer les lignes de commande
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

        //  Vérifier et réserver le stock
        boolean allReserved = reserveStock(order);

        if (allReserved) {
            order.setStatus(OrderStatus.RESERVED);
        } else {
            // la commande reste en CREATED
            order.setStatus(OrderStatus.CREATED);

        }

        //  Sauvegarder la commande
        salesOrderRepository.save(order);

        //  Retourner la réponse
        return salesOrderMapper.toResponse(order);
    }


    private boolean reserveStock(SalesOrder order) {
        boolean allReserved = true;

        for (SalesOrderLine line : order.getOrderLines()) {
            int quantityToReserve = line.getQuantity();
            List<Warehouse> warehouses = warehouseRepository.findAll();

            Inventory fallbackInventory = null; // inventaire où mettre qtyReserved si besoin

            for (Warehouse warehouse : warehouses) {
                Optional<Inventory> optionalInventory = inventoryRepository.findByWarehouseAndProduct(warehouse, line.getProduct());
                if (optionalInventory.isEmpty()) {
                    continue;
                }

                Inventory inventory = optionalInventory.get();
                // garder une référence pour fallback même si qtyOnHand == 0
                if (fallbackInventory == null) {
                    fallbackInventory = inventory;
                }

                int available = inventory.getQtyOnHand();
                if (available <= 0) continue;

                int reservedQty = Math.min(available, quantityToReserve);

                // mise à jour du stock physique
                inventory.setQtyOnHand(available - reservedQty);
                inventoryRepository.save(inventory);

                // créer mouvement OUTBOUND pour la quantité réellement réservée physiquement
                String reference = "Order-" + (order.getId() != null ? order.getId() : "temp");
                String desc = "Réservation du produit '" + line.getProduct().getName() +
                        "' depuis l'entrepôt '" + warehouse.getName() + "'";
                inventoryMovementService.createOutboundMovement(inventory, reservedQty, reference, desc);

                quantityToReserve -= reservedQty;

                if (quantityToReserve == 0) break; // produit complètement réservé physiquement
            }

            // Si après tous les entrepôts, la quantité reste insuffisante
            if (quantityToReserve > 0) {
                allReserved = false;
                line.setBackorder(true);

                // placer la quantité restante dans qtyReserved d'un inventaire ayant le produit
                if (fallbackInventory != null) {
                    int currentReserved = fallbackInventory.getQtyReserved() != null ? fallbackInventory.getQtyReserved() : 0;
                    fallbackInventory.setQtyReserved(currentReserved + quantityToReserve);
                    inventoryRepository.save(fallbackInventory);
                } else {
                    // Aucun inventaire trouvé pour ce produit : lancer une exception explicite
                    throw new CustomException("Aucun inventaire trouvé pour réserver la quantité restante du produit: " + line.getProduct().getName());
                }
            }
        }
        return allReserved;
    }
}
