
package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.CrearTransaccionNequiDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class WompiTransaccionService {
    @Value("${wompi.public-key}")
    private String publicKey;

    @Value("${wompi.private-key}")
    private String privateKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode crearTransaccionNequi(CrearTransaccionNequiDTO dto) throws Exception {
        String url = "https://sandbox.wompi.co/v1/transactions";

        Map<String, Object> body = new HashMap<>();
        body.put("acceptance_token", dto.getAcceptanceToken());
        body.put("accept_personal_auth", dto.getAcceptPersonalAuth());
        body.put("amount_in_cents", dto.getAmountInCents());
        body.put("currency", dto.getCurrency());
        body.put("customer_email", dto.getCustomerEmail());
        body.put("reference", dto.getReference());
        body.put("payment_method_type", "NEQUI");

        Map<String, String> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "NEQUI");
        paymentMethod.put("phone_number", dto.getPhoneNumber());
        body.put("payment_method", paymentMethod);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(privateKey); // importante

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return objectMapper.readTree(response.getBody());
    }
}
