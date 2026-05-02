package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.dto.PresignedUrlResponse;
import com.warehouse.warehouse_manager.dto.PresignedUrlsRequest;
import com.warehouse.warehouse_manager.dto.SignatureFileUploadResponse;
import com.warehouse.warehouse_manager.services.FileSignatureUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/signatures")
@RequiredArgsConstructor
public class AdminSignatureFileController {

    private final FileSignatureUploadService fileSignatureUploadService;

    @PostMapping("/upload")
    public ResponseEntity<SignatureFileUploadResponse> uploadSignatureFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("threatName") String threatName,
            @RequestParam("fileType") String fileType,
            @RequestParam("offsetStart") long offsetStart,
            @RequestParam("offsetEnd") long offsetEnd
    ) {
        SignatureFileUploadResponse response =
                fileSignatureUploadService.uploadAndCreateSignature(
                        file,
                        threatName,
                        fileType,
                        offsetStart,
                        offsetEnd
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/presigned-urls")
    public ResponseEntity<List<PresignedUrlResponse>> getPresignedUrls(
            @RequestBody PresignedUrlsRequest request
    ) {
        List<PresignedUrlResponse> response =
                fileSignatureUploadService.getPresignedUrls(request.getIds());

        return ResponseEntity.ok(response);
    }
}