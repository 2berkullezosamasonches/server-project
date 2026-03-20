package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.dto.TicketResponse;
import com.warehouse.warehouse_manager.model.License;
import com.warehouse.warehouse_manager.model.User;
import com.warehouse.warehouse_manager.repository.UserRepository;
import com.warehouse.warehouse_manager.services.LicenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/licenses")
@RequiredArgsConstructor
public class LicenseController {

    private final LicenseService licenseService;
    private final UserRepository userRepository;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<License> createLicense(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(licenseService.createLicense(
                Long.valueOf(request.get("productId").toString()),
                Long.valueOf(request.get("ownerId").toString()),
                Long.valueOf(request.get("typeId").toString()),
                Integer.valueOf(request.get("deviceCount").toString())));
    }

    @PostMapping("/activate")
    public ResponseEntity<?> activateLicense(@RequestBody Map<String, String> request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(licenseService.activateLicense(
                    request.get("code"), request.get("deviceMac"), request.get("deviceName"), getCurrentUser(userDetails)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/renew")
    public ResponseEntity<?> renewLicense(@RequestBody Map<String, String> request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(licenseService.renewLicense(request.get("code"), getCurrentUser(userDetails)));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/check")
    public ResponseEntity<?> checkLicense(@RequestBody Map<String, Object> request, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(licenseService.checkLicense(
                    request.get("deviceMac").toString(), Long.valueOf(request.get("productId").toString()), getCurrentUser(userDetails)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        }
    }

    private User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
    }
}