package com.warehouse.warehouse_manager.binary.builder;

import com.warehouse.warehouse_manager.binary.dto.ManifestEntry;
import com.warehouse.warehouse_manager.binary.protocol.BinaryProtocolConstants;
import com.warehouse.warehouse_manager.binary.protocol.ExportType;
import com.warehouse.warehouse_manager.binary.util.BinaryWriter;

import java.util.List;

public class BinaryManifestBuilder {

    public byte[] build(
            ExportType exportType,
            long generatedAtEpochMillis,
            long sinceEpochMillis,
            int recordCount,
            byte[] dataSha256,
            List<ManifestEntry> entries,
            byte[] manifestSignature
    ) {
        BinaryWriter writer = new BinaryWriter();

        writer.writeString(BinaryProtocolConstants.MANIFEST_MAGIC);
        writer.writeUInt16(BinaryProtocolConstants.MANIFEST_VERSION);
        writer.writeUInt8(exportType.getCode());
        writer.writeInt64(generatedAtEpochMillis);
        writer.writeInt64(sinceEpochMillis);
        writer.writeUInt32(recordCount);
        writer.writeBytes(dataSha256);

        for (ManifestEntry entry : entries) {
            writer.writeUuid(entry.getId());
            writer.writeUInt8(entry.getStatusCode());
            writer.writeInt64(entry.getUpdatedAt());
            writer.writeUInt32(entry.getDataOffset());
            writer.writeUInt32(entry.getDataLength());
            writer.writeByteArray(entry.getSignatureBytes());
        }

        writer.writeByteArray(manifestSignature);

        return writer.toByteArray();
    }

    public byte[] buildUnsigned(
            ExportType exportType,
            long generatedAtEpochMillis,
            long sinceEpochMillis,
            int recordCount,
            byte[] dataSha256,
            List<ManifestEntry> entries
    ) {
        BinaryWriter writer = new BinaryWriter();

        writer.writeString(BinaryProtocolConstants.MANIFEST_MAGIC);
        writer.writeUInt16(BinaryProtocolConstants.MANIFEST_VERSION);
        writer.writeUInt8(exportType.getCode());
        writer.writeInt64(generatedAtEpochMillis);
        writer.writeInt64(sinceEpochMillis);
        writer.writeUInt32(recordCount);
        writer.writeBytes(dataSha256);

        for (ManifestEntry entry : entries) {
            writer.writeUuid(entry.getId());
            writer.writeUInt8(entry.getStatusCode());
            writer.writeInt64(entry.getUpdatedAt());
            writer.writeUInt32(entry.getDataOffset());
            writer.writeUInt32(entry.getDataLength());
            writer.writeByteArray(entry.getSignatureBytes());
        }

        return writer.toByteArray();
    }
}