package com.zoha.docanalyser.service;

import com.zoha.docanalyser.model.Analysis;
import com.zoha.docanalyser.model.AnalysisType;
import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.repository.AnalysisRepository;
import com.zoha.docanalyser.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final DocumentRepository documentRepository;
    private final AIService aiService;

    public AnalysisService(AnalysisRepository analysisRepository, 
                          DocumentRepository documentRepository,
                          AIService aiService) {
        this.analysisRepository = analysisRepository;
        this.documentRepository = documentRepository;
        this.aiService = aiService;
    }

    @Transactional
    public Analysis analyzeDocument(Long documentId, AnalysisType type) {
        // Find the document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        // Check if document has extracted text
        if (document.getExtractedText() == null || document.getExtractedText().isEmpty()) {
            throw new RuntimeException("Document has no extracted text to analyze");
        }
        
        // Check if analysis already exists
        var existingAnalysis = analysisRepository.findByDocumentAndAnalysisType(document, type);
        if (existingAnalysis.isPresent()) {
            log.info("Analysis already exists for document {} and type {}", documentId, type);
            return existingAnalysis.get();
        }
        
        // Perform AI analysis based on type
        String result = performAnalysis(document.getExtractedText(), type);
        
        // Save and return
        Analysis analysis = new Analysis(document, type, result);
        return analysisRepository.save(analysis);
    }
    
    private String performAnalysis(String text, AnalysisType type) {
        switch (type) {
            case SUMMARY:
                return aiService.summarize(text);
            case KEY_POINTS:
                return aiService.extractKeyPoints(text);
            case ENTITIES:
                return aiService.extractEntities(text);
            default:
                throw new IllegalArgumentException("Unknown analysis type: " + type);
        }
    }
}