package com.warehouse.warehouse_manager.binary.util;

public class HexUtils {

    public static byte[] fromHex(String hex) {
        if (hex == null || hex.isBlank()) {
            return new byte[0];
        }

        String normalized = hex
                .trim()
                .replace(" ", "")
                .replace("0x", "")
                .replace("0X", "");

        if (normalized.length() % 2 != 0) {
            normalized = "0" + normalized;
        }

        byte[] result = new byte[normalized.length() / 2];

        for (int i = 0; i < normalized.length(); i += 2) {
            int high = Character.digit(normalized.charAt(i), 16);
            int low = Character.digit(normalized.charAt(i + 1), 16);

            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character: " + normalized);
            }

            result[i / 2] = (byte) ((high << 4) + low);
        }

        return result;
    }
}