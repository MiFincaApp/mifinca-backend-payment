
package com.mifinca.payment.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class SignatureVerifier {
    @Value("${wompi.integrity-secret}")
    private String integritySecret;

    public boolean isValid(String rawBody, String receivedSignature) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(integritySecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);

            byte[] hashBytes = sha256_HMAC.doFinal(rawBody.getBytes());
            String calculatedSignature = Base64.getEncoder().encodeToString(hashBytes);

            return calculatedSignature.equals(receivedSignature);
        } catch (Exception e) {
            return false;
        }
    }
}
