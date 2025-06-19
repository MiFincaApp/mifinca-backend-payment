package com.mifinca.payment.controller;

import com.mifinca.payment.dto.TokensAceptacionDTO;
import com.mifinca.payment.dto.TransaccionNequiRequest;
import com.mifinca.payment.dto.TransaccionNequiResponse;
import com.mifinca.payment.service.TransaccionPagoService;
import com.mifinca.payment.service.WompiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wompi")
public class WompiController {

    private final WompiService wompiService;
    private final TransaccionPagoService transaccionPagoService;


    public WompiController(WompiService wompiService,  TransaccionPagoService transaccionPagoService) {
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
    public ResponseEntity<TransaccionNequiResponse> crearTransaccionNequi(@RequestBody TransaccionNequiRequest request) {
        try {
            TransaccionNequiResponse response = transaccionPagoService.crearTransaccionNequi(
                    request.getCelular(),
                    request.getReferencia(),
                    request.getCorreoCliente(),
                    request.getAcceptanceToken(),
                    request.getPersonalToken()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
