package com.zoha.docanalyser.dto.gemini;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationConfig {
        private int maxOutputTokens;
        private double temperature;
    }
    
    // Factory method for easy creation
    public static GeminiRequest create(String prompt, int maxTokens) {
        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
        GenerationConfig config = new GenerationConfig(maxTokens, 0.7);
        return new GeminiRequest(List.of(content), config);
    }
}