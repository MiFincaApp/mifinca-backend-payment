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
    public ResponseEntity<TransaccionNequiResponse> crearTransaccionNequi(
            @RequestBody TransaccionNequiRequest dto
    ) {
        try {
            TransaccionNequiResponse response = transaccionPagoService.crearTransaccionNequi(dto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/transaccion/{id}")
    public ResponseEntity<TransaccionNequiResponse> consultarEstadoTransaccion(@PathVariable("id") String idTransaccion) {
        try {
            TransaccionNequiResponse response = transaccionPagoService.consultarEstadoTransaccion(idTransaccion);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
