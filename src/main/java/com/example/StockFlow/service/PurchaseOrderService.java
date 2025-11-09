package com.example.StockFlow.service;

import com.example.StockFlow.dto.request.PurchaseOrderRequest;
import com.example.StockFlow.dto.response.PurchaseOrderResponse;
import com.example.StockFlow.dto.response.UserResponse;
import com.example.StockFlow.entity.*;
import com.example.StockFlow.entity.enums.MovementType;
import com.example.StockFlow.entity.enums.PurchaseOrderStatus;
import com.example.StockFlow.exception.CustomException;
import com.example.StockFlow.mapper.PurchaseOrderLineMapper;
import com.example.StockFlow.mapper.PurchaseOrderMapper;
import com.example.StockFlow.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderLineMapper purchaseOrderLineMapper;
    private final ManagerRepository managerRepository;
    private final AuthService authService;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    @Transactional
    public PurchaseOrderResponse createOrder(PurchaseOrderRequest request, String token) {
        // 🔹 1. Récupérer l’utilisateur courant
        UserResponse userResponse = authService.getCurrentUser(token).getUser();

        // 🔹 2. Récupérer le manager
        Manager manager = managerRepository.findById(userResponse.getId())
                .orElseThrow(() -> new CustomException("Manager introuvable"));

        // 🔹 3. Récupérer le fournisseur
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Fournisseur non trouvé"));

        // 🔹 4. Créer la commande (sans lignes)
        PurchaseOrder order = purchaseOrderMapper.toEntity(request, supplier, manager);
        order.setStatus(PurchaseOrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setWarehouseId(request.getWarehouseId());

        // 🔹 5. Mapper les lignes avec calculs
        var lines = request.getOrderLines().stream().map(lineRequest -> {
            var product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

            var line = purchaseOrderLineMapper.toEntity(lineRequest, product);
            line.setPurchaseOrder(order);

            // 💡 Calculs déplacés ici
            BigDecimal unitPrice = product.getOriginalPrice() != null ? product.getOriginalPrice() : BigDecimal.ZERO;
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(lineRequest.getQuantity()));

            line.setUnitPrice(unitPrice);
            line.setTotalPrice(totalPrice);
            line.setCreatedAt(LocalDateTime.now());


            return line;
        }).toList();

        // 🔹 6. Associer les lignes à la commande
        order.setOrderLines(lines);

        // 🔹 7. Sauvegarder
        purchaseOrderRepository.save(order);

        // 🔹 8. Retourner la réponse
        return purchaseOrderMapper.toResponse(order);
    }
    @Transactional
    public PurchaseOrderResponse approvePurchaseOrder(Long purchaseOrderId) {
        // 1️⃣ Récupérer la commande
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));

        // 2️⃣ Vérifier si déjà approuvée
        if (purchaseOrder.getStatus() == PurchaseOrderStatus.APPROVED) {
            throw new RuntimeException("Purchase order already approved");
        }

        // 3️⃣ Récupérer le warehouse concerné
        Long warehouseId = purchaseOrder.getWarehouseId();
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        // 4️⃣ Récupérer les lignes de commande (produits commandés)
        List<PurchaseOrderLine> lines = purchaseOrder.getOrderLines();

        for (PurchaseOrderLine line : lines) {
            Product product = line.getProduct();
            Integer qtyReceived = line.getQuantity();

            // 5️⃣ Vérifier si un inventaire existe déjà pour ce produit dans ce warehouse
            Inventory inventory = inventoryRepository
                    .findByWarehouseAndProduct(warehouse, product)
                    .orElseGet(() -> {
                        // sinon créer un nouvel inventaire
                        Inventory newInv = Inventory.builder()
                                .warehouse(warehouse)
                                .product(product)
                                .name(product.getName() + " - " + warehouse.getName())
                                .qtyOnHand(0)
                                .qtyReserved(0)
                                .build();
                        return inventoryRepository.save(newInv);
                    });

            // 6️⃣ Ajouter la quantité reçue
            inventory.setQtyOnHand(inventory.getQtyOnHand() + qtyReceived);
            inventoryRepository.save(inventory);

            // 7️⃣ Enregistrer un mouvement INBOUND
            InventoryMovement movement = InventoryMovement.builder()
                    .inventory(inventory)
                    .type(MovementType.INBOUND)
                    .qty(qtyReceived)
                    .occurredAt(LocalDateTime.now())
                    .referenceDocument("PO-" + purchaseOrder.getId())
                    .description("Réception de commande fournisseur")
                    .build();

            inventoryMovementRepository.save(movement);
        }

        // 8️⃣ Changer le statut de la commande
        purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
        purchaseOrderRepository.save(purchaseOrder);
        return purchaseOrderMapper.toResponse(purchaseOrder);

    }
}
