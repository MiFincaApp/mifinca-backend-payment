package com.mifinca.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pagos")
public class WompiPaymentLinkController {

    @Value("${wompi.public-key}")
    private String publicKey;

    @Value("${wompi.private-key}")
    private String privateKey;

    @Value("${wompi.base-url}")
    private String wompiBaseUrl;

    @Value("${wompi.redirect-url}")
    private String redirectUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/crear-link")
    public ResponseEntity<?> crearLinkPago(@RequestBody Map<String, Object> datosPago) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("name", "Pago en MiFincaApp");
            payload.put("description", "Pago desde app móvil");
            payload.put("amount_in_cents", datosPago.get("monto_centavos"));
            payload.put("currency", "COP");
            payload.put("reference", datosPago.get("referencia"));
            payload.put("customer_email", datosPago.get("correo_cliente"));
            payload.put("single_use", true);
            payload.put("redirect_url", redirectUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + privateKey);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    wompiBaseUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED || response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String urlPago = responseJson.path("data").path("url").asText();
                return ResponseEntity.ok(Map.of("url_pago", urlPago));
            }

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creando link de pago");
        }
    }
}
