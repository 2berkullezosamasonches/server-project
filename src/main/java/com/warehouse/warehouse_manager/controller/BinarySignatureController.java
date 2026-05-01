package com.warehouse.warehouse_manager.controller;

import com.warehouse.warehouse_manager.binary.dto.BinaryPackage;
import com.warehouse.warehouse_manager.dto.BinarySignaturesByIdsRequest;
import com.warehouse.warehouse_manager.services.BinarySignatureService;
import com.warehouse.warehouse_manager.services.MultipartMixedResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/binary/signatures")
@RequiredArgsConstructor
public class BinarySignatureController {

    private final BinarySignatureService binarySignatureService;
    private final MultipartMixedResponseFactory multipartMixedResponseFactory;

    @GetMapping("/full")
    public ResponseEntity<MultiValueMap<String, Object>> full() {
        BinaryPackage binaryPackage = binarySignatureService.buildFullPackage();

        return multipartMixedResponseFactory.create(
                binaryPackage.getManifest(),
                binaryPackage.getData()
        );
    }

    @GetMapping("/increment")
    public ResponseEntity<MultiValueMap<String, Object>> increment(
            @RequestParam("since") String since
    ) {
        OffsetDateTime parsedSince = OffsetDateTime.parse(since);

        BinaryPackage binaryPackage = binarySignatureService.buildIncrementPackage(parsedSince);

        return multipartMixedResponseFactory.create(
                binaryPackage.getManifest(),
                binaryPackage.getData()
        );
    }

    @PostMapping("/by-ids")
    public ResponseEntity<MultiValueMap<String, Object>> byIds(
            @RequestBody BinarySignaturesByIdsRequest request
    ) {
        BinaryPackage binaryPackage = binarySignatureService.buildByIdsPackage(request.getIds());

        return multipartMixedResponseFactory.create(
                binaryPackage.getManifest(),
                binaryPackage.getData()
        );
    }
}