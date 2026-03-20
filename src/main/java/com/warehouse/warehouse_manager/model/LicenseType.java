package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "license_types")
@Data
@NoArgsConstructor
public class LicenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer defaultDuration; // Длительность в днях по умолчанию
}