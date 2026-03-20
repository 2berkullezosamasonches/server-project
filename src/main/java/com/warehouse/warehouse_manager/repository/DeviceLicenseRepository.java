package com.warehouse.warehouse_manager.repository;

import com.warehouse.warehouse_manager.model.DeviceLicense;
import com.warehouse.warehouse_manager.model.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceLicenseRepository extends JpaRepository<DeviceLicense, Long> {
    // Метод для подсчета текущих активаций по лицензии
    long countByLicense(License license);
}