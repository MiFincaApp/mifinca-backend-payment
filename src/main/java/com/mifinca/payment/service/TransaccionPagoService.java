package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.TransaccionNequiRequest;
import com.mifinca.payment.dto.TransaccionNequiResponse;
import com.mifinca.payment.entity.TransaccionPago;
import com.mifinca.payment.repository.TransaccionPagoRepository;
import com.mifinca.payment.util.ReferenciaPagoGenerator;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

    @Value("${wompi.integrity-secret}")
    private String integritySecret;

    public TransaccionNequiResponse crearTransaccionNequi(TransaccionNequiRequest dto) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // === Extraer datos del DTO ===
            String celular = dto.getCelular();
            String correo = dto.getCorreoCliente();
            int montoEnCentavos = dto.getMonto_en_centavos();
            String acceptanceToken = dto.getAcceptanceToken();
            String personalToken = dto.getPersonalToken();
            String nombre = dto.getNombreCompleto();
            String tipoDoc = dto.getTipoDocumento();
            String numeroDoc = dto.getNumeroDocumento();
            String direccion = dto.getDireccion();
            String ciudad = dto.getCiudad();
            String departamento = dto.getDepartamento();

            // === Generar referencia única ===
            String referencia = ReferenciaPagoGenerator.generarReferenciaUnica("prueba-app-movil");

            // === Calcular firma de integridad ===
            String rawString = referencia + montoEnCentavos + "COP" + integritySecret;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawString.getBytes(StandardCharsets.UTF_8));
            String firmaHex = HexFormat.of().formatHex(hash);

            // === Construir payload ===
            Map<String, Object> payload = new HashMap<>();
            payload.put("payment_method", Map.of("type", "NEQUI", "phone_number", celular));
            payload.put("amount_in_cents", montoEnCentavos);
            payload.put("currency", "COP");
            payload.put("customer_email", correo);
            payload.put("reference", referencia);
            payload.put("acceptance_token", acceptanceToken);
            payload.put("payment_description", "Pago desde app móvil");
            payload.put("redirect_url", null);
            payload.put("signature", firmaHex);

            // customer_data (para dashboard de Wompi)
            Map<String, Object> customerData = new HashMap<>();
            customerData.put("full_name", nombre);
            customerData.put("phone_number", celular);
            customerData.put("phone_number_prefix", "+57");
            customerData.put("legal_id", numeroDoc);
            customerData.put("legal_id_type", tipoDoc);
            payload.put("customer_data", customerData);

            // shipping_address (opcional, pero útil para reportes)
            Map<String, Object> shippingAddress = new HashMap<>();
            shippingAddress.put("address_line_1", direccion);
            shippingAddress.put("address_line_2", "");
            shippingAddress.put("country", "CO");
            shippingAddress.put("city", ciudad);
            shippingAddress.put("region", departamento);
            shippingAddress.put("phone_number", celular);
            shippingAddress.put("name", nombre);
            payload.put("shipping_address", shippingAddress);

            // === Enviar solicitud a Wompi ===
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(wompiPrivateKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    "https://sandbox.wompi.co/v1/transactions",
                    request,
                    JsonNode.class
            );

            JsonNode data = response.getBody().path("data");

            // === Guardar en base de datos ===
            TransaccionPago nueva = new TransaccionPago();
            nueva.setIdTransaccionWompi(data.path("id").asText());
            nueva.setMontoCentavos(data.path("amount_in_cents").asInt());
            nueva.setMoneda(data.path("currency").asText());
            nueva.setEstado(data.path("status").asText());
            nueva.setMetodoPago("NEQUI");
            nueva.setReferencia(referencia);
            nueva.setCorreoCliente(correo);
            nueva.setFechaCreacion(LocalDateTime.now());
            nueva.setFechaActualizacion(LocalDateTime.now());

            // Nuevos campos personales
            nueva.setNombreCompleto(nombre);
            nueva.setTipoDocumento(tipoDoc);
            nueva.setNumeroDocumento(numeroDoc);
            nueva.setDireccion(direccion);
            nueva.setCiudad(ciudad);
            nueva.setDepartamento(departamento);

            repository.save(nueva);

            return new TransaccionNequiResponse(nueva.getIdTransaccionWompi(), nueva.getEstado(), nueva.getReferencia());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear transacción NEQUI");
        }
    }

    @Value("${wompi.verificar-url}")
    private String verificarUrl; // ejemplo: https://sandbox.wompi.co/v1/transactions/

    public TransaccionNequiResponse consultarEstadoTransaccion(String idTransaccion) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(wompiPrivateKey);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            String url = verificarUrl + idTransaccion;

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    JsonNode.class
            );

            JsonNode data = response.getBody().path("data");
            String estadoActual = data.path("status").asText();

            String referencia = null;

            Optional<TransaccionPago> opt = repository.findByIdTransaccionWompi(idTransaccion);
            if (opt.isPresent()) {
                TransaccionPago tp = opt.get();
                referencia = tp.getReferencia();

                if (!tp.getEstado().equalsIgnoreCase(estadoActual)) {
                    tp.setEstado(estadoActual);
                    tp.setFechaActualizacion(LocalDateTime.now());
                    repository.save(tp);
                }
            }

            return new TransaccionNequiResponse(idTransaccion, estadoActual, referencia);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al consultar estado de transacción");
        }
    }

}
