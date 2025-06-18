package com.mifinca.payment.controller;

import com.mifinca.payment.util.SignatureVerifier;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

import java.util.Map;

@RestController
@RequestMapping("/webhook/wompi")
public class WompiWebhookController {

    private final SignatureVerifier signatureVerifier;

    public WompiWebhookController(SignatureVerifier signatureVerifier) {
        this.signatureVerifier = signatureVerifier;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> handleWebhook(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            StringBuilder rawBodyBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                rawBodyBuilder.append(line);
            }
            String rawBody = rawBodyBuilder.toString();

            System.out.println("=== WEBHOOK RECIBIDO ===");
            System.out.println("Raw Body:");
            System.out.println(rawBody);

            boolean isValid = signatureVerifier.isValid(rawBody);
            if (!isValid) {
                response.put("status", "error");
                response.put("message", "firma de evento no válida");
                return ResponseEntity.ok(response);
            }

            // Aquí podrías procesar el evento
            response.put("status", "ok");
            response.put("message", "webhook recibido correctamente");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "excepción controlada, revisa logs");
            return ResponseEntity.ok(response);
        }
    }

}
