package com.Backend.controller;

import com.Backend.dto.AiRequestDTO;
import com.Backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateStudyMaterial(@RequestBody AiRequestDTO request) {
        try {
            // 1. The "System Prompt": We dynamically inject the group's context into the AI's instructions.
            String prompt = String.format(
                "You are an expert university tutor. Your student is at an %s level in %s. " +
                "They want to study the topic: '%s'. " +
                "Create 5 high-quality %s for them. " +
                "CRITICAL INSTRUCTION: You MUST respond ONLY with a valid JSON array of objects. " +
                "Each object must have exactly two keys: 'title' (the question/concept) and 'content' (the answer/explanation). " +
                "Do not include any conversational text, do not include markdown formatting like ```json, just output the raw JSON array starting with [ and ending with ].",
                request.getSkillLevel(), request.getSubject(), request.getTopic(), request.getMaterialType()
            );

            // 2. Send the highly specific prompt to Google
            String rawJsonResponse = geminiService.generateContent(prompt);

            // 3. Safety Check: Sometimes LLMs ignore the rules and wrap JSON in markdown anyway.
            // This strips out any accidental markdown tags so React doesn't choke on it.
            String cleanedResponse = rawJsonResponse.replace("```json", "")
                                                    .replace("```", "")
                                                    .trim();

            // 4. Return the pure JSON string
            return ResponseEntity.ok(cleanedResponse);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to generate AI content: " + e.getMessage()));
        }
    }
}