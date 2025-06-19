package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.NequiTransactionRequest;
import com.mifinca.payment.dto.TokensAceptacionDTO;
import com.mifinca.payment.entity.TransaccionPago;
import com.mifinca.payment.repository.TransaccionPagoRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WompiService {

    @Value("${wompi.public-key}")
    private String publicKey;

    @Value("${wompi.private-key}")
    private String privateKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokensAceptacionDTO obtenerTokensDeAceptacion() throws Exception {
        String url = "https://sandbox.wompi.co/v1/merchants/" + publicKey;

        String json = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(json);

        JsonNode acceptance = root.path("data").path("presigned_acceptance");
        JsonNode personal = root.path("data").path("presigned_personal_data_auth");

        TokensAceptacionDTO dto = new TokensAceptacionDTO();
        dto.setAcceptanceToken(acceptance.path("acceptance_token").asText());
        dto.setAcceptancePermalink(acceptance.path("permalink").asText());
        dto.setPersonalToken(personal.path("acceptance_token").asText());
        dto.setPersonalPermalink(personal.path("permalink").asText());

        return dto;
    }

    @Autowired
    private TransaccionPagoRepository transaccionPagoRepository;

    public JsonNode crearTransaccionNequi(NequiTransactionRequest req) throws Exception {
        // Armar payload
        Map<String, Object> body = new HashMap<>();
        body.put("acceptance_token", req.getAcceptanceToken());
        body.put("accept_personal_auth", req.getAcceptPersonalAuth());
        body.put("amount_in_cents", req.getAmountInCents());
        body.put("currency", req.getCurrency());
        body.put("customer_email", req.getCustomerEmail());
        body.put("reference", req.getReference());

        Map<String, Object> paymentMethod = new HashMap<>();
        paymentMethod.put("type", "NEQUI");
        paymentMethod.put("phone_number", req.getPhoneNumber());
        body.put("payment_method", paymentMethod);
        body.put("payment_method_type", "NEQUI");

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(privateKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Consumir endpoint Wompi
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://sanbox.wompi.co/v1/transactions", // o sandbox si estás en pruebas
                requestEntity,
                String.class
        );

        JsonNode json = objectMapper.readTree(response.getBody());
        JsonNode transaction = json.path("data");

        // Guardar en base de datos
        TransaccionPago tx = new TransaccionPago();
        tx.setIdTransaccionWompi(transaction.path("id").asText());
        tx.setMontoCentavos(transaction.path("amount_in_cents").asInt());
        tx.setMoneda(transaction.path("currency").asText());
        tx.setEstado("PENDIENTE");
        tx.setMetodoPago("NEQUI");
        tx.setReferencia(transaction.path("reference").asText());
        tx.setCorreoCliente(transaction.path("customer_email").asText());
        tx.setFechaCreacion(LocalDateTime.now());
        tx.setFechaActualizacion(LocalDateTime.now());

        transaccionPagoRepository.save(tx);

        return json;
    }
}
