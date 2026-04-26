package com.jobportal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAnalyzerService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    public String analyzeResume(String resumeText) {
        if ("YOUR_GROQ_API_KEY_HERE".equals(groqApiKey) || groqApiKey == null || groqApiKey.trim().isEmpty()) {
            throw new RuntimeException("Groq API key is missing or not configured. Please configure it in application.properties.");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            String prompt = "You are an expert technical recruiter analyzing a resume. Extract the following from this resume text: skills, strengths, weak areas, and 3 suggested job roles. " +
                    "Return ONLY a valid JSON object with exactly these four keys: 'skills' (array of strings), 'strengths' (array of strings), 'weakAreas' (array of strings), 'suggestedJobs' (array of strings). Do not return any other text or markdown formatting.\n\nResume Text:\n" + resumeText;

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", List.of(message));
            requestBody.put("model", "llama-3.1-8b-instant");
            requestBody.put("temperature", 0.1);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(GROQ_API_URL, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                String content = root.path("choices").get(0).path("message").path("content").asText();
                return content;
            } else {
                throw new RuntimeException("Failed to analyze resume from Groq API: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Error during AI analysis: " + e.getMessage());
        }
    }
}
