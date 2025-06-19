
package com.mifinca.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mifinca.payment.dto.CrearTransaccionNequiDTO;
import com.mifinca.payment.service.WompiTransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wompi/transaccion")
public class WompiTransaccionController {
    private final WompiTransaccionService transaccionService;

    public WompiTransaccionController(WompiTransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @PostMapping("/nequi")
    public ResponseEntity<?> crearTransaccionNequi(@RequestBody CrearTransaccionNequiDTO dto) {
        try {
            JsonNode respuesta = transaccionService.crearTransaccionNequi(dto);
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al crear la transacción: " + e.getMessage());
        }
    }
}
