
package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.entity.TransaccionPago;
import com.mifinca.payment.repository.TransaccionPagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TransaccionPagoService {
    
     private final TransaccionPagoRepository repository;
    private final ObjectMapper objectMapper;

    public TransaccionPagoService(TransaccionPagoRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void guardarDesdeEvento(String rawBody) {
        try {
            JsonNode root = objectMapper.readTree(rawBody);
            JsonNode transaction = root.path("data").path("transaction");

            TransaccionPago tx = new TransaccionPago();
            tx.setIdTransaccionWompi(transaction.path("id").asText());
            tx.setMontoCentavos(transaction.path("amount_in_cents").asInt());
            tx.setMoneda(transaction.path("currency").asText());
            tx.setEstado(transaction.path("status").asText());
            tx.setMetodoPago(transaction.path("payment_method_type").asText(null));
            tx.setReferencia(transaction.path("reference").asText(null));
            tx.setCorreoCliente(transaction.path("customer_email").asText(null));
            tx.setFechaCreacion(LocalDateTime.now());

            repository.save(tx);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
