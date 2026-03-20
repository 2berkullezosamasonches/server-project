package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LicenseRepository extends JpaRepository<License, Long> {
    Optional<License> findByCode(String code);
}