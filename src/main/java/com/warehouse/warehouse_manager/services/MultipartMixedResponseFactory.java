package com.warehouse.warehouse_manager.services;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
public class MultipartMixedResponseFactory {

    public ResponseEntity<MultiValueMap<String, Object>> create(byte[] manifest, byte[] data) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("manifest", createPart("manifest.bin", manifest));
        body.add("data", createPart("data.bin", data));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("multipart/mixed"))
                .body(body);
    }

    private HttpEntity<ByteArrayResource> createPart(String filename, byte[] bytes) {
        ByteArrayResource resource = new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(filename)
                        .build()
        );
        headers.setContentLength(bytes.length);

        return new HttpEntity<>(resource, headers);
    }
}