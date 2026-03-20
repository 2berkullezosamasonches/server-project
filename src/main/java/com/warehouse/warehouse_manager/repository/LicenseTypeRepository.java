package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.LicenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseTypeRepository extends JpaRepository<LicenseType, Long> {
}