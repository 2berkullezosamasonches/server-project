package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "licenses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Лицензионный ключ (UUID)

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private LicenseType licenseType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; // Тот, кто купил/создал

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Тот, кто активировал

    private LocalDateTime firstActivationDate;
    private LocalDateTime endingDate;

    private Boolean blocked = false;

    @Column(nullable = false)
    private Integer deviceCount; // Лимит устройств
}