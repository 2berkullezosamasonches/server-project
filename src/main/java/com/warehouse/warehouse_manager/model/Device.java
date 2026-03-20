package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String macAddress;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Владелец устройства
}