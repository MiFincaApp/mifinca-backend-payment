package com.mifinca.payment.controller;

import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook/wompi")
public class WompiWebhookController {

    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody Map<String, Object> payload) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("message", "webhook recibido");

        return ResponseEntity.ok(response);
    }

}
