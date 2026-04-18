package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.SignaturePayload;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignatureHashingService {

    private final RSASigningService rsaSigningService;

    public String generateDigitalSignature(MalwareSignature sig) {
        SignaturePayload payload = SignaturePayload.builder()
                .threatName(sig.getThreatName())
                .firstBytesHex(sig.getFirstBytesHex())
                .remainderHashHex(sig.getRemainderHashHex())
                .remainderLength(sig.getRemainderLength())
                .fileType(sig.getFileType())
                .offsetStart(sig.getOffsetStart())
                .offsetEnd(sig.getOffsetEnd())
                .status(sig.getStatus())
                .build();

        return rsaSigningService.signSignaturePayload(payload);
    }
}