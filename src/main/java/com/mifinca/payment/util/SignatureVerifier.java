package com.mifinca.payment.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Iterator;

@Component
public class SignatureVerifier {

    @Value("${wompi.event-secret}")
    private String eventSecret;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isValid(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);

            // Leer el array de propiedades
            JsonNode properties = root.at("/signature/properties");
            StringBuilder concatenated = new StringBuilder();

            for (JsonNode prop : properties) {
                // por ejemplo: transaction.status → busca el valor
                String[] pathParts = prop.asText().split("\\.");
                JsonNode valueNode = root.get("data");
                for (String part : pathParts) {
                    valueNode = valueNode.get(part);
                }
                concatenated.append(valueNode.asText());
            }

            // Concatenar el timestamp
            String timestamp = root.get("timestamp").asText();
            concatenated.append(timestamp);

            // Concatenar el event secret
            concatenated.append(eventSecret);

            // Generar checksum con SHA256
            String calculatedChecksum = sha256Hex(concatenated.toString());

            // Comparar con el checksum recibido
            String receivedChecksum = root.at("/signature/checksum").asText();

            return calculatedChecksum.equalsIgnoreCase(receivedChecksum);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String sha256Hex(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        // Convertir a hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }
}
