package com.warehouse.warehouse_manager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Аннотация @JsonIgnore скрывает хеш пароля из всех JSON-ответов API.
     * Это критически важно для защиты данных (Data Exposure Prevention).
     */
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    // Связь с лицензиями, которыми владеет пользователь
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @JsonIgnore // Чтобы не было бесконечной рекурсии в JSON
    private List<License> ownedLicenses;

    // Связь с историей (для аудита)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<LicenseHistory> history;
}