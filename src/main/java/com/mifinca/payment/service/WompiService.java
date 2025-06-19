
package com.mifinca.payment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mifinca.payment.dto.TokensAceptacionDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WompiService {
    @Value("${wompi.public-key}")
    private String publicKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokensAceptacionDTO obtenerTokensDeAceptacion() throws Exception {
        String url = "https://sandbox.wompi.co/v1/merchants/" + publicKey;

        String json = restTemplate.getForObject(url, String.class);
        JsonNode root = objectMapper.readTree(json);

        JsonNode acceptance = root.path("data").path("presigned_acceptance");
        JsonNode personal = root.path("data").path("presigned_personal_data_auth");

        TokensAceptacionDTO dto = new TokensAceptacionDTO();
        dto.setAcceptanceToken(acceptance.path("acceptance_token").asText());
        dto.setAcceptancePermalink(acceptance.path("permalink").asText());
        dto.setPersonalToken(personal.path("acceptance_token").asText());
        dto.setPersonalPermalink(personal.path("permalink").asText());

        return dto;
    }
}
