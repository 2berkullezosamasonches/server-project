package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.binary.builder.BinaryDataBuilder;
import com.warehouse.warehouse_manager.binary.builder.BinaryManifestBuilder;
import com.warehouse.warehouse_manager.binary.dto.BinaryDataBuildResult;
import com.warehouse.warehouse_manager.binary.dto.BinaryPackage;
import com.warehouse.warehouse_manager.binary.dto.BinarySignatureRecord;
import com.warehouse.warehouse_manager.binary.protocol.ExportType;
import com.warehouse.warehouse_manager.binary.protocol.SignatureStatusCode;
import com.warehouse.warehouse_manager.binary.util.HashUtils;
import com.warehouse.warehouse_manager.binary.util.HexUtils;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import com.warehouse.warehouse_manager.model.SignatureStatus;
import com.warehouse.warehouse_manager.repository.MalwareSignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BinarySignatureService {

    private final MalwareSignatureRepository malwareSignatureRepository;
    private final RSASigningService rsaSigningService;

    private final BinaryDataBuilder binaryDataBuilder = new BinaryDataBuilder();
    private final BinaryManifestBuilder binaryManifestBuilder = new BinaryManifestBuilder();

    public BinaryPackage buildFullPackage() {
        List<MalwareSignature> signatures =
                malwareSignatureRepository.findByStatus(SignatureStatus.ACTUAL);

        return buildPackage(signatures, ExportType.FULL, -1);
    }

    public BinaryPackage buildIncrementPackage(OffsetDateTime since) {
        List<MalwareSignature> signatures =
                malwareSignatureRepository.findByUpdatedAtAfter(since);

        long sinceEpochMillis = since.toInstant().toEpochMilli();

        return buildPackage(signatures, ExportType.INCREMENT, sinceEpochMillis);
    }

    public BinaryPackage buildByIdsPackage(List<UUID> ids) {
        List<MalwareSignature> signatures =
                malwareSignatureRepository.findAllById(ids);

        return buildPackage(signatures, ExportType.BY_IDS, -1);
    }

    private BinaryPackage buildPackage(
            List<MalwareSignature> signatures,
            ExportType exportType,
            long sinceEpochMillis
    ) {
        List<BinarySignatureRecord> records = signatures.stream()
                .map(this::toBinaryRecord)
                .toList();

        BinaryDataBuildResult dataResult = binaryDataBuilder.build(records);

        byte[] dataBytes = dataResult.getData();
        byte[] dataSha256 = HashUtils.sha256(dataBytes);

        long generatedAtEpochMillis = Instant.now().toEpochMilli();

        byte[] unsignedManifest = binaryManifestBuilder.buildUnsigned(
                exportType,
                generatedAtEpochMillis,
                sinceEpochMillis,
                records.size(),
                dataSha256,
                dataResult.getEntries()
        );

        byte[] manifestSignature = rsaSigningService.signBytes(unsignedManifest);

        byte[] manifestBytes = binaryManifestBuilder.build(
                exportType,
                generatedAtEpochMillis,
                sinceEpochMillis,
                records.size(),
                dataSha256,
                dataResult.getEntries(),
                manifestSignature
        );

        BinaryPackage binaryPackage = new BinaryPackage();
        binaryPackage.setManifest(manifestBytes);
        binaryPackage.setData(dataBytes);

        return binaryPackage;
    }

    private BinarySignatureRecord toBinaryRecord(MalwareSignature signature) {
        BinarySignatureRecord record = new BinarySignatureRecord();

        record.setId(signature.getId());
        record.setStatusCode(toStatusCode(signature.getStatus()));
        record.setUpdatedAt(signature.getUpdatedAt().toInstant().toEpochMilli());

        record.setThreatName(signature.getThreatName());
        record.setFirstBytes(HexUtils.fromHex(signature.getFirstBytesHex()));
        record.setRemainderHash(HexUtils.fromHex(signature.getRemainderHashHex()));
        record.setRemainderLength(signature.getRemainderLength());
        record.setFileType(signature.getFileType());
        record.setOffsetStart(signature.getOffsetStart());
        record.setOffsetEnd(signature.getOffsetEnd());

        byte[] signatureBytes = Base64.getDecoder()
                .decode(signature.getDigitalSignatureBase64());

        record.setSignatureBytes(signatureBytes);

        return record;
    }

    private int toStatusCode(SignatureStatus status) {
        if (status == SignatureStatus.ACTUAL) {
            return SignatureStatusCode.ACTUAL.getCode();
        }

        if (status == SignatureStatus.DELETED) {
            return SignatureStatusCode.DELETED.getCode();
        }

        throw new IllegalArgumentException("Unknown signature status: " + status);
    }
}