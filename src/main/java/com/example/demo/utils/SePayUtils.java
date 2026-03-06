package com.example.demo.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class SePayUtils {
    public static String makeSignature(Map<String, String> fields, String secretKey) throws Exception {
        // THỨ TỰ "VÀNG" RÚT RA TỪ VÍ DỤ CỦA DOCS [cite: 2026-03-04]
        String[] signableFields = {
                "merchant",
                "operation",
                "order_amount",
                "currency",
                "order_invoice_number",
                "order_description",
                "customer_id",
                "success_url",
                "error_url",
                "cancel_url"
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < signableFields.length; i++) {
            String key = signableFields[i];
            String value = fields.getOrDefault(key, "");
            sb.append(key).append("=").append(value);
            if (i < signableFields.length - 1) {
                sb.append(",");
            }
        }

        String dataToSign = sb.toString();
        // System.out.println("Chuỗi ký thực tế: " + dataToSign);

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] rawHmac = mac.doFinal(dataToSign.getBytes());

        return Base64.getEncoder().encodeToString(rawHmac);

    }
}
