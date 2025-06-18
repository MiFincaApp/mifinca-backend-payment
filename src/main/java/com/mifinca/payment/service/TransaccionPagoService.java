package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.entity.TransaccionPago;
import com.mifinca.payment.repository.TransaccionPagoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
                    // Si ya existe y el estado no cambió, no hacemos nada
                    System.out.println("Transacción ya registrada sin cambio de estado: " + idTransaccion);
                    return;
                }
                
                // Si ya existe actualizar
                existente.setEstado(estado);
                existente.setMetodoPago(metodoPago);
                existente.setReferencia(referencia);
                existente.setCorreoCliente(correo);
                existente.setFechaActualizacion(LocalDateTime.now());
                existente.setTimestampEvento(timestampEvento);

                repository.save(existente);
                System.out.println("Transacción actualizada: " + idTransaccion);
            } else {
                // Si no existe guardar nuevo
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
}
