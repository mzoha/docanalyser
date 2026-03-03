package com.zoha.docanalyser.service;

import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ComparisonService {

    private final DocumentRepository documentRepository;
    private final AIService aiService;

    public ComparisonService(DocumentRepository documentRepository, AIService aiService) {
        this.documentRepository = documentRepository;
        this.aiService = aiService;
    }

    public String compareDocuments(Long documentId1, Long documentId2) {
        // Retrieve both documents
        Document doc1 = documentRepository.findById(documentId1)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId1));
        
        Document doc2 = documentRepository.findById(documentId2)
            .orElseThrow(() -> new RuntimeException("Document not found: " + documentId2));
        
        // Check if both have extracted text
        if (doc1.getExtractedText() == null || doc1.getExtractedText().isEmpty()) {
            throw new RuntimeException("Document 1 has no extracted text: " + doc1.getFilename());
        }
        
        if (doc2.getExtractedText() == null || doc2.getExtractedText().isEmpty()) {
            throw new RuntimeException("Document 2 has no extracted text: " + doc2.getFilename());
        }
        
        log.info("Comparing document {} with document {}", documentId1, documentId2);
        
        // Perform comparison
        return aiService.compareDocuments(doc1.getExtractedText(), doc2.getExtractedText());
    }
}