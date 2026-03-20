package com.warehouse.warehouse_manager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "license_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LicenseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "license_id", nullable = false)
    private License license;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String status; // CREATED, ACTIVATED, RENEWED

    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;

    @Column(length = 500)
    private String description;
}