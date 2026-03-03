package com.Backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// Bring back Jackson for flawless JSON unescapingSS
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt) {
        String urlWithKey = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("parts", List.of(Map.of("text", prompt)));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(message));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    urlWithKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // ✨ Let Jackson handle all the complex escape characters safely!
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            
            // Navigate directly to the AI's text response
            JsonNode textNode = rootNode.at("/candidates/0/content/parts/0/text");
            
            if (!textNode.isMissingNode()) {
                // .asText() automatically unescapes the quotes, newlines, and special characters
                String cleanJson = textNode.asText();
                
                // Strip the markdown formatting tags the AI sometimes wraps its JSON in
                cleanJson = cleanJson.replace("```json\n", "")
                                     .replace("```json", "")
                                     .replace("```", "")
                                     .trim();
                                     
                return cleanJson;
            }
            
            return "[]"; 

        } catch (Exception e) {
            System.err.println("API ERROR: " + e.getMessage());
            return "[]"; 
        }
    }
}