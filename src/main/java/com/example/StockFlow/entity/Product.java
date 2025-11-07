package com.example.StockFlow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String category;

    private Boolean active;

    private Long originalPrice;

    private BigDecimal profit;

    // Relation avec Inventory : un produit peut être dans plusieurs inventaires
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories;

    // Relation avec SalesOrderLine : un produit peut apparaître dans plusieurs lignes de commande
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesOrderLine> salesOrderLines;

    // Relation avec PurchaseOrderLine : un produit peut apparaître dans plusieurs lignes de commande fournisseur
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderLine> purchaseOrderLines;
}
