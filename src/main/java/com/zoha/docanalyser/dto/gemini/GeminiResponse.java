package com.zoha.docanalyser.dto.gemini;

import lombok.Data;
import java.util.List;

@Data
public class GeminiResponse {
    private List<Candidate> candidates;
    private PromptFeedback promptFeedback;
    
    @Data
    public static class Candidate {
        private Content content;
        private String finishReason;
        private int index;
    }
    
    @Data
    public static class Content {
        private List<Part> parts;
        private String role;
    }
    
    @Data
    public static class Part {
        private String text;
    }
    
    @Data
    public static class PromptFeedback {
        private String blockReason;
    }
    
    // Helper to extract text from response
    public String getExtractedText() {
        if (candidates != null && !candidates.isEmpty() 
                && candidates.get(0).getContent() != null 
                && candidates.get(0).getContent().getParts() != null
                && !candidates.get(0).getContent().getParts().isEmpty()) {
            return candidates.get(0).getContent().getParts().get(0).getText();
        }
        return "";
    }
}