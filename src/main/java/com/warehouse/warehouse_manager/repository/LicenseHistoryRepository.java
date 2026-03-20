package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.LicenseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseHistoryRepository extends JpaRepository<LicenseHistory, Long> {
}