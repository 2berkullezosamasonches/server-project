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

    private final KeyProvider keyProvider;
    private final CanonicalizationService canonicalizationService;

    public String signTicket(Ticket ticket) throws Exception {
        // 1. Получаем каноничные байты
        byte[] data = canonicalizationService.canonicalize(ticket);

        // 2. Достаем ключ
        PrivateKey privateKey = keyProvider.getPrivateKey();

        // 3. Подписываем (SHA256withRSA)
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(data);

        // 4. Кодируем результат в Base64
        return Base64.getEncoder().encodeToString(rsa.sign());
    }
}