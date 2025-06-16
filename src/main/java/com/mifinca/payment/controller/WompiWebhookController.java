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
    public ResponseEntity<Map<String, String>> handleWebhook(HttpServletRequest request,
                                                             @RequestHeader("x-signature") String xSignature) {
        try {
            // Leer el cuerpo crudo
            StringBuilder rawBodyBuilder = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                rawBodyBuilder.append(line);
            }
            String rawBody = rawBodyBuilder.toString();

            //Imprimir datos clave para depurar
            System.out.println("=== WEBHOOK RECIBIDO ===");
            System.out.println("Raw Body:");
            System.out.println(rawBody);
            System.out.println("Firma recibida:");
            System.out.println(xSignature);

            // Calcular firma localmente
            String firmaCalculada = signatureVerifier.generateSignature(rawBody);
            System.out.println("Firma calculada:");
            System.out.println(firmaCalculada);
            
            // Validar firma
            boolean isValid = signatureVerifier.isValid(rawBody, xSignature);
            if (!isValid) {
                Map<String, String> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "firma no válida");
                return ResponseEntity.badRequest().body(error);
            }

            // Si es válida, responder
            Map<String, String> response = new HashMap<>();
            response.put("status", "ok");
            response.put("message", "webhook recibido");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "error interno");
            return ResponseEntity.status(500).body(error);
        }
    }
}
