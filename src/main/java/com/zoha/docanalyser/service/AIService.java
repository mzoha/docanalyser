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

    // Add these methods to AIService.java

    /**
     * Detailed analysis of document structure and style
     */
    public String detailedAnalysis(String text) {
        String prompt = """
        Provide a detailed analysis of the following document including:
        1. Main topics discussed
        2. Key arguments or conclusions
        3. Writing style analysis (formal/informal, technical, persuasive, etc.)
        4. Target audience assessment
        5. Document structure and flow
        
        Format the response with clear section headings.
        
        Document:
        """ + text;

        return callGemini(prompt, 1000);
    }

    /**
     * Extract main topics and themes
     */
    public String extractTopics(String text) {
        String prompt = """
        Identify the main topics and themes in this document.
        For each topic, provide:
        - Topic name
        - Relevance (high/medium/low)
        - Key related concepts
        
        Format as a structured list.
        
        Document:
        """ + text;

        return callGemini(prompt, 800);
    }

    /**
     * Analyze sentiment and tone
     */
    public String analyzeSentiment(String text) {
        String prompt = """
        Analyze the sentiment and tone of this document:
        
        1. Overall sentiment (positive/negative/neutral)
        2. Sentiment score (1-10)
        3. Emotional tone (e.g., urgent, optimistic, critical, informative)
        4. Key emotional phrases
        5. Sentiment changes throughout the document
        
        Document:
        """ + text;

        return callGemini(prompt, 600);
    }

    /**
     * Generate discussion questions
     */
    public String generateQuestions(String text) {
        String prompt = """
        Based on this document, generate:
        
        1. 5 discussion questions for deeper exploration
        2. 3 quiz questions to test comprehension
        3. 1 thought-provoking question for further research
        
        Document:
        """ + text;

        return callGemini(prompt, 700);
    }

    /**
     * Analyze target audience
     */
    public String analyzeTargetAudience(String text) {
        String prompt = """
        Analyze the target audience for this document:
        
        1. Primary audience (demographics, role, expertise level)
        2. Secondary audiences
        3. Assumed prior knowledge
        4. Language complexity level
        5. What the audience is expected to do with this information
        
        Document:
        """ + text;

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

    public String compareDocuments(String text1, String text2) {
        String prompt = """
        Compare these two documents in detail:
        
        DOCUMENT 1:
        """ + text1 + """
        
        DOCUMENT 2:
        """ + text2 + """
        
        Provide a comprehensive comparison including:
        
        1. SIMILARITIES:
           - Main topics in common
           - Similar arguments or conclusions
           - Comparable writing styles
        
        2. DIFFERENCES:
           - Unique topics in each
           - Contrasting viewpoints
           - Different approaches or focuses
        
        3. UNIQUE STRENGTHS:
           - What each document does better
        
        4. OVERALL ASSESSMENT:
           - Which document is more comprehensive?
           - Which is more accessible to readers?
           - Key takeaways from comparing them
        
        Format with clear section headings and bullet points.
        """;

        return callGemini(prompt, 1500);
    }
}