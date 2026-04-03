package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.model.MalwareSignature;
import com.warehouse.warehouse_manager.model.SignatureStatus;
import com.warehouse.warehouse_manager.services.CanonicalizationService;
import com.warehouse.warehouse_manager.services.RSASigningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestSignatureController {

    private final CanonicalizationService canonicalizationService;
    private final RSASigningService rsaSigningService;

    @GetMapping("/check-crypto")
    public Map<String, Object> checkCrypto() throws Exception {
        // 1. Создаем "кукольную" сигнатуру с большим числом
        MalwareSignature sig = MalwareSignature.builder()
                .id(UUID.randomUUID())
                .threatName("EICAR-Test-Signature")
                .firstBytesHex("4D5A")
                .remainderHashHex("ef534234")
                .remainderLength(1000000000L) // Тот самый миллиард для проверки
                .fileType("EXE")
                .offsetStart(0)
                .offsetEnd(512)
                .status(SignatureStatus.ACTUAL)
                .updatedAt(Instant.now())
                .build();

        // 2. Проверяем канонизацию (что получилось внутри байтов)
        byte[] canonicalBytes = canonicalizationService.canonicalizeSignature(sig);
        String jsonString = new String(canonicalBytes);

        // 3. Проверяем подпись
        String signature = rsaSigningService.signMalwareSignature(sig);

        return Map.of(
                "1_canonical_json", jsonString, // Смотрим порядок полей и миллиард
                "2_signature_base64", signature, // Смотрим, что RSA выдал строку
                "3_status", "Success"
        );
    }
}