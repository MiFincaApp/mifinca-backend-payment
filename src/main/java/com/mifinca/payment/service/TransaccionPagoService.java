package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.TransaccionNequiResponse;
import com.mifinca.payment.entity.TransaccionPago;
import com.mifinca.payment.repository.TransaccionPagoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TransaccionPagoService {

    private final TransaccionPagoRepository repository;
    private final ObjectMapper objectMapper;

    @Value("${wompi.private-key}")
    private String wompiPrivateKey;

    public TransaccionPagoService(TransaccionPagoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void guardarDesdeEvento(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            JsonNode transaction = root.path("data").path("transaction");

            String idTransaccion = transaction.path("id").asText();
            int monto = transaction.path("amount_in_cents").asInt();
            String moneda = transaction.path("currency").asText();
            String estado = transaction.path("status").asText();
            String metodoPago = transaction.path("payment_method_type").asText(null);
            String referencia = transaction.path("reference").asText(null);
            String correo = transaction.path("customer_email").asText(null);
            long timestampEvento = root.path("timestamp").asLong();

            Optional<TransaccionPago> existenteOpt = repository.findByIdTransaccionWompi(idTransaccion);

            if (existenteOpt.isPresent()) {
                TransaccionPago existente = existenteOpt.get();

                if (estado.equalsIgnoreCase(existente.getEstado())) {
                    System.out.println("Transacción ya registrada sin cambio de estado: " + idTransaccion);
                    return;
                }

                existente.setEstado(estado);
                existente.setMetodoPago(metodoPago);
                existente.setReferencia(referencia);
                existente.setCorreoCliente(correo);
                existente.setFechaActualizacion(LocalDateTime.now());
                existente.setTimestampEvento(timestampEvento);

                repository.save(existente);
                System.out.println("Transacción actualizada: " + idTransaccion);
            } else {
                TransaccionPago nuevo = new TransaccionPago();
                nuevo.setIdTransaccionWompi(idTransaccion);
                nuevo.setMontoCentavos(monto);
                nuevo.setMoneda(moneda);
                nuevo.setEstado(estado);
                nuevo.setMetodoPago(metodoPago);
                nuevo.setReferencia(referencia);
                nuevo.setCorreoCliente(correo);
                nuevo.setFechaCreacion(LocalDateTime.now());
                nuevo.setFechaActualizacion(LocalDateTime.now());
                nuevo.setTimestampEvento(timestampEvento);

                repository.save(nuevo);
                System.out.println("Transacción nueva guardada: " + idTransaccion);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TransaccionNequiResponse crearTransaccionNequi(
            String celular,
            String referencia,
            String correo,
            String acceptanceToken,
            String personalToken,
            int montoEnCentavos // ← nuevo parámetro
    ) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> payload = new HashMap<>();
            payload.put("payment_method", Map.of("type", "NEQUI", "phone_number", celular));
            payload.put("amount_in_cents", montoEnCentavos); // ← usa el valor recibido
            payload.put("currency", "COP");
            payload.put("customer_email", correo);
            payload.put("reference", referencia);
            payload.put("acceptance_token", acceptanceToken);
            payload.put("payment_description", "Pago desde app móvil");
            payload.put("redirect_url", null);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(wompiPrivateKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://sandbox.wompi.co/v1/transactions", request, JsonNode.class
            );

            JsonNode data = response.getBody().path("data");

            TransaccionPago nueva = new TransaccionPago();
            nueva.setIdTransaccionWompi(data.path("id").asText());
            nueva.setMontoCentavos(montoEnCentavos);
            nueva.setMoneda(data.path("currency").asText());
            nueva.setEstado(data.path("status").asText());
            nueva.setMetodoPago("NEQUI");
            nueva.setReferencia(referencia);
            nueva.setCorreoCliente(correo);
            nueva.setFechaCreacion(LocalDateTime.now());
            nueva.setFechaActualizacion(LocalDateTime.now());

            repository.save(nueva);

            return new TransaccionNequiResponse(nueva.getIdTransaccionWompi(), nueva.getEstado());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear transacción NEQUI");
        }
    }

}
