package com.example.StockFlow.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "managers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Manager extends User {

    private String department;
    private Boolean active;

    @OneToMany(mappedBy = "manager", orphanRemoval = false)
    private List<Warehouse> warehouses;

    @OneToMany(mappedBy = "manager")
    private List<PurchaseOrder> purchaseOrders;




}
