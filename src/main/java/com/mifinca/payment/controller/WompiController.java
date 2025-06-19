package com.mifinca.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.TokensAceptacionDTO;
import com.mifinca.payment.dto.TransaccionNequiRequest;
import com.mifinca.payment.dto.TransaccionNequiResponse;
import com.mifinca.payment.service.TransaccionPagoService;
import com.mifinca.payment.service.WompiService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wompi")
public class WompiController {

    private final WompiService wompiService;
    private final TransaccionPagoService transaccionPagoService;

    public WompiController(WompiService wompiService, TransaccionPagoService transaccionPagoService) {
        this.wompiService = wompiService;
        this.transaccionPagoService = transaccionPagoService;
    }

    @GetMapping("/aceptacion")
    public ResponseEntity<TokensAceptacionDTO> obtenerTokensAceptacion() {
        try {
            TokensAceptacionDTO dto = wompiService.obtenerTokensDeAceptacion();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transaccion/nequi")
    public ResponseEntity<TransaccionNequiResponse> crearTransaccionNequi(HttpServletRequest request) {
        try {
            // Leer el cuerpo crudo
            String rawBody = new String(request.getInputStream().readAllBytes());

            // Parsear a DTO
            ObjectMapper mapper = new ObjectMapper();
            TransaccionNequiRequest dto = mapper.readValue(rawBody, TransaccionNequiRequest.class);

            // Convertir el monto a int
            int montoEnCentavos = dto.getMonto_en_centavos();

            // Llamar al servicio
            TransaccionNequiResponse response = transaccionPagoService.crearTransaccionNequi(
                    dto.getCelular(),
                    dto.getReferencia(),
                    dto.getCorreoCliente(),
                    dto.getAcceptanceToken(),
                    dto.getPersonalToken(),
                    montoEnCentavos
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
