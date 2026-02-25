package com.zoha.docanalyser.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class PDFService {

    /**
     * Extracts text content from a PDF file
     * @param pdfFile the PDF file to process
     * @return extracted text as String
     * @throws IOException if file is corrupted, encrypted, or not a valid PDF
     */
    public String extractText(File pdfFile) throws IOException {
        // Using try-with-resources to ensure PDDocument is closed properly
        try (PDDocument document = PDDocument.load(pdfFile)) {
            
            // Check if document is encrypted
            if (document.isEncrypted()) {
                throw new IOException("PDF is encrypted and cannot be processed");
            }
            
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            log.info("Successfully extracted {} characters from PDF: {}", 
                text.length(), pdfFile.getName());
            
            return text;
            
        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", pdfFile.getName(), e);
            throw new IOException("Error processing PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets the number of pages in a PDF
     * @param pdfFile the PDF file
     * @return number of pages
     */
    public int getPageCount(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            return document.getNumberOfPages();
        }
    }
}