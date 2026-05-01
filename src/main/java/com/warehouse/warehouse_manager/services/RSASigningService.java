package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.SignaturePayload;
import com.warehouse.warehouse_manager.dto.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RSASigningService {

    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final KeyProvider keyProvider;
    private final CanonicalizationService canonicalizationService;

    public String signTicket(Ticket ticket) {
        return signObject(ticket, "Ошибка формирования ЭЦП тикета");
    }

    public String signSignaturePayload(SignaturePayload payload) {
        return signObject(payload, "Ошибка формирования ЭЦП сигнатуры");
    }

    public byte[] signBytes(byte[] data) {
        try {
            PrivateKey privateKey = keyProvider.getPrivateKey();

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);

            return signature.sign();
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка формирования ЭЦП бинарного манифеста: " + e.getMessage(), e);
        }
    }

    private String signObject(Object payload, String errorMessage) {
        try {
            byte[] data = canonicalizationService.canonicalize(payload);
            PrivateKey privateKey = keyProvider.getPrivateKey();

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException(errorMessage + ": " + e.getMessage(), e);
        }
    }
}