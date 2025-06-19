package com.mifinca.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mifinca.payment.dto.NequiTransactionRequest;
import com.mifinca.payment.dto.TokensAceptacionDTO;
import com.mifinca.payment.service.WompiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wompi")
public class WompiController {

    private final WompiService wompiService;

    public WompiController(WompiService wompiService) {
        this.wompiService = wompiService;
    }

    @GetMapping("/aceptacion")
    public ResponseEntity<TokensAceptacionDTO> obtenerTokensAceptacion() {
        try {
            TokensAceptacionDTO dto = wompiService.obtenerTokensDeAceptacion();
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/transaccion/nequi")
    public ResponseEntity<?> crearTransaccionNequi(@RequestBody NequiTransactionRequest request) {
        try {
            JsonNode result = wompiService.crearTransaccionNequi(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear transacción NEQUI: " + e.getMessage());
        }
    }
}
