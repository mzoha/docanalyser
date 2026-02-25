package com.zoha.docanalyser.service;

import com.zoha.docanalyser.model.Document;
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

@Service
@Slf4j
public class DocumentService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
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
        // status defaults to UPLOADED
        return documentRepository.save(document);
    }
}