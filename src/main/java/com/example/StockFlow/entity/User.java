package com.example.StockFlow.entity;

import com.example.StockFlow.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String addressse;

    private String tel;

    @Column(nullable = false, name = "password")

    private String password;



    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
