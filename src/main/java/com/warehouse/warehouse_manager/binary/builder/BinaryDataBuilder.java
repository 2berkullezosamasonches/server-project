package com.warehouse.warehouse_manager.binary.builder;

import com.warehouse.warehouse_manager.binary.dto.BinaryDataBuildResult;
import com.warehouse.warehouse_manager.binary.dto.BinarySignatureRecord;
import com.warehouse.warehouse_manager.binary.dto.ManifestEntry;
import com.warehouse.warehouse_manager.binary.protocol.BinaryProtocolConstants;
import com.warehouse.warehouse_manager.binary.util.BinaryWriter;

import java.util.ArrayList;
import java.util.List;

public class BinaryDataBuilder {

    public BinaryDataBuildResult build(List<BinarySignatureRecord> records) {
        BinaryWriter writer = new BinaryWriter();

        writer.writeString(BinaryProtocolConstants.DATA_MAGIC);
        writer.writeUInt16(BinaryProtocolConstants.DATA_VERSION);
        writer.writeUInt32(records.size());

        List<ManifestEntry> entries = new ArrayList<>();

        int payloadStart = writer.size();

        for (BinarySignatureRecord record : records) {
            int recordStart = writer.size();

            writeRecord(writer, record);

            int recordEnd = writer.size();

            ManifestEntry entry = new ManifestEntry();
            entry.setId(record.getId());
            entry.setStatusCode(record.getStatusCode());
            entry.setUpdatedAt(record.getUpdatedAt());
            entry.setDataOffset(recordStart - payloadStart);
            entry.setDataLength(recordEnd - recordStart);
            entry.setSignatureBytes(record.getSignatureBytes());

            entries.add(entry);
        }

        BinaryDataBuildResult result = new BinaryDataBuildResult();
        result.setData(writer.toByteArray());
        result.setEntries(entries);

        return result;
    }

    private void writeRecord(BinaryWriter writer, BinarySignatureRecord record) {
        writer.writeString(record.getThreatName());
        writer.writeByteArray(record.getFirstBytes());
        writer.writeByteArray(record.getRemainderHash());
        writer.writeInt64(record.getRemainderLength()); // ← исправлено
        writer.writeString(record.getFileType());
        writer.writeInt64(record.getOffsetStart());
        writer.writeInt64(record.getOffsetEnd());
    }
}