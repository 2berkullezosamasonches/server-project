package com.warehouse.warehouse_manager.services;

import com.warehouse.warehouse_manager.dto.PresignedUrlResponse;
import com.warehouse.warehouse_manager.dto.SignatureFileUploadResponse;
import com.warehouse.warehouse_manager.model.MalwareSignature;
import com.warehouse.warehouse_manager.model.SignatureStatus;
import com.warehouse.warehouse_manager.repository.MalwareSignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileSignatureUploadService {

    private static final int FIRST_BYTES_LENGTH = 16;

    private final MalwareSignatureRepository malwareSignatureRepository;
    private final SignatureHashingService signatureHashingService;
    private final MinioFileStorageService minioFileStorageService;

    @Transactional
    public SignatureFileUploadResponse uploadAndCreateSignature(
            MultipartFile file,
            String threatName,
            String fileType,
            long offsetStart,
            long offsetEnd
    ) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Файл не должен быть пустым");
            }

            if (threatName == null || threatName.isBlank()) {
                throw new IllegalArgumentException("threatName обязателен");
            }

            if (fileType == null || fileType.isBlank()) {
                throw new IllegalArgumentException("fileType обязателен");
            }

            if (offsetStart < 0) {
                throw new IllegalArgumentException("offsetStart не может быть меньше 0");
            }

            if (offsetEnd < offsetStart) {
                throw new IllegalArgumentException("offsetEnd не может быть меньше offsetStart");
            }

            byte[] fileBytes = file.getBytes();

            int firstBytesLength = Math.min(FIRST_BYTES_LENGTH, fileBytes.length);

            byte[] firstBytes = Arrays.copyOfRange(fileBytes, 0, firstBytesLength);
            byte[] remainderBytes = Arrays.copyOfRange(fileBytes, firstBytesLength, fileBytes.length);

            String firstBytesHex = HexFormat.of().formatHex(firstBytes).toUpperCase();
            String remainderHashHex = sha256Hex(remainderBytes);
            long remainderLength = remainderBytes.length;

            String objectKey = minioFileStorageService.uploadFile(file);

            MalwareSignature signature = new MalwareSignature();
            signature.setId(UUID.randomUUID());
            signature.setThreatName(threatName);
            signature.setFirstBytesHex(firstBytesHex);
            signature.setRemainderHashHex(remainderHashHex);
            signature.setRemainderLength(remainderLength);
            signature.setFileType(fileType);
            signature.setOffsetStart(offsetStart);
            signature.setOffsetEnd(offsetEnd);
            signature.setUpdatedAt(OffsetDateTime.now());
            signature.setStatus(SignatureStatus.ACTUAL);

            signature.setSourceFileObjectKey(objectKey);
            signature.setSourceFileName(file.getOriginalFilename());
            signature.setSourceFileContentType(file.getContentType());
            signature.setSourceFileSize(file.getSize());

            signature.setDigitalSignatureBase64(
                    signatureHashingService.generateDigitalSignature(signature)
            );

            MalwareSignature saved = malwareSignatureRepository.save(signature);

            SignatureFileUploadResponse response = new SignatureFileUploadResponse();
            response.setSignatureId(saved.getId());
            response.setThreatName(saved.getThreatName());
            response.setFileName(saved.getSourceFileName());
            response.setObjectKey(saved.getSourceFileObjectKey());
            response.setFileSize(saved.getSourceFileSize());

            return response;
        } catch (Exception e) {
            throw new IllegalStateException("Ошибка загрузки файла и создания сигнатуры: " + e.getMessage(), e);
        }
    }

    public List<PresignedUrlResponse> getPresignedUrls(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("Список ids не должен быть пустым");
        }

        List<PresignedUrlResponse> result = new ArrayList<>();

        for (UUID id : ids) {
            MalwareSignature signature = malwareSignatureRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Сигнатура не найдена: " + id));

            if (signature.getSourceFileObjectKey() == null || signature.getSourceFileObjectKey().isBlank()) {
                throw new IllegalStateException("Для сигнатуры нет файла в MinIO: " + id);
            }

            String url = minioFileStorageService.getPresignedUrl(signature.getSourceFileObjectKey());

            PresignedUrlResponse response = new PresignedUrlResponse();
            response.setSignatureId(signature.getId());
            response.setFileName(signature.getSourceFileName());
            response.setUrl(url);

            result.add(response);
        }

        return result;
    }

    private String sha256Hex(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return HexFormat.of().formatHex(hash).toUpperCase();
    }
}