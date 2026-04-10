package com.warehouse.warehouse_manager.services;

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
        try {
            byte[] data = canonicalizationService.canonicalize(ticket);
            PrivateKey privateKey = keyProvider.getPrivateKey();

            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(data);

            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка формирования ЭЦП тикета: " + e.getMessage(), e);
        }
    }
}