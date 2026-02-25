package com.zoha.docanalyser.service;

import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.model.ProcessingStatus;
import com.zoha.docanalyser.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@Slf4j
public class DocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final PDFService pdfService;

    public DocumentService(DocumentRepository documentRepository, PDFService pdfService) {
        this.documentRepository = documentRepository;
        this.pdfService = pdfService;
    }

    public Document uploadDocument(MultipartFile file) throws IOException {
        // 1. Validate file (type, size)
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        // 2. Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 3. Generate a unique filename to avoid collisions
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(uniqueFilename);

        // 4. Save file to disk
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("File saved to: {}", filePath);

        // 5. Create Document entity and save to DB
        Document document = new Document();
        document.setFilename(originalFilename);
        document.setFilePath(filePath.toString());
        document.setStatus(ProcessingStatus.PROCESSING);

        // Save initially to get ID
        Document savedDocument = documentRepository.save(document);

        try {
            // 6. Extract text from PDF
            log.info("Starting text extraction for document ID: {}", savedDocument.getId());
            String extractedText = pdfService.extractText(filePath.toFile());

            // 7. Update document with extracted text
            savedDocument.setExtractedText(extractedText);
            savedDocument.setStatus(ProcessingStatus.COMPLETED);
            log.info("Text extraction completed for document ID: {}", savedDocument.getId());

        } catch (IOException e) {
            // 8. Handle extraction failure
            log.error("Text extraction failed for document ID: {}", savedDocument.getId(), e);
            savedDocument.setStatus(ProcessingStatus.FAILED);
            savedDocument.setExtractedText("Error: " + e.getMessage());
        }

        // 9. Save final state
        return documentRepository.save(savedDocument);
    }

    public Document getDocument(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + id));
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
}