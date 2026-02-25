package com.zoha.docanalyser.service;

import com.zoha.docanalyser.dto.gemini.GeminiRequest;
import com.zoha.docanalyser.dto.gemini.GeminiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
@Slf4j
public class AIService {

    private final WebClient geminiWebClient;
    private static final int MAX_RETRIES = 3;
    private static final int TIMEOUT_SECONDS = 30;

    public AIService(@Qualifier("geminiWebClient") WebClient geminiWebClient) {
        this.geminiWebClient = geminiWebClient;
    }

    /**
     * Generate a summary of the given text
     */
    public String summarize(String text) {
        String prompt = "Please provide a concise summary of the following text in 3-5 bullet points:\n\n" + text;
        return callGemini(prompt, 500);
    }

    /**
     * Extract key points from the given text
     */
    public String extractKeyPoints(String text) {
        String prompt = "Extract the 10 most important key points from the following text. Format as bullet points:\n\n" + text;
        return callGemini(prompt, 800);
    }

    /**
     * Extract entities (people, organizations, dates, locations) from text
     */
    public String extractEntities(String text) {
        String prompt = "Extract all people, organizations, dates, and locations from the following text. Format as a structured list with categories:\n\n" + text;
        return callGemini(prompt, 600);
    }

    /**
     * Core method to call Gemini API with retry logic
     */
    private String callGemini(String prompt, int maxTokens) {
        GeminiRequest request = GeminiRequest.create(prompt, maxTokens);
        
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                attempt++;
                log.info("Calling Gemini API, attempt {}", attempt);
                
                GeminiResponse response = geminiWebClient.post()
                        .bodyValue(request)
                        .retrieve()
                        .bodyToMono(GeminiResponse.class)
                        .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                        .onErrorResume(e -> {
                            log.error("Error calling Gemini API", e);
                            return Mono.error(new RuntimeException("Gemini API call failed: " + e.getMessage()));
                        })
                        .block();
                
                if (response != null) {
                    String result = response.getExtractedText();
                    if (!result.isEmpty()) {
                        log.info("Gemini API call successful, got {} characters", result.length());
                        return result;
                    }
                }
                
                throw new RuntimeException("Empty response from Gemini API");
                
            } catch (Exception e) {
                log.warn("Attempt {} failed: {}", attempt, e.getMessage());
                
                if (attempt == MAX_RETRIES) {
                    log.error("All {} retry attempts failed", MAX_RETRIES);
                    return "Error: Unable to get response from AI service. Please try again later.";
                }
                
                // Exponential backoff: wait longer between retries
                try {
                    long waitTime = (long) Math.pow(2, attempt) * 1000;
                    log.info("Waiting {} ms before retry...", waitTime);
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        return "Error: AI service unavailable after multiple attempts.";
    }
}