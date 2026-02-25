package com.zoha.docanalyser.controller;

import com.zoha.docanalyser.model.Analysis;
import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.service.DocumentService;
import com.zoha.docanalyser.repository.AnalysisRepository;  // Keep this for analyses
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class DocumentController {

    private final DocumentService documentService;      // Use Service, not Repository
    private final AnalysisRepository analysisRepository; // Keep this for analyses

    // Constructor with both
    public DocumentController(DocumentService documentService,
                              AnalysisRepository analysisRepository) {
        this.documentService = documentService;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            Document savedDocument = documentService.uploadDocument(file);
            redirectAttributes.addFlashAttribute("message",
                    "File uploaded successfully! Document ID: " + savedDocument.getId());
            return "redirect:/documents/" + savedDocument.getId();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to store file: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/documents/{id}")
    public String viewDocument(@PathVariable Long id, Model model) {
        try {
            Document document = documentService.getDocument(id);  // You'll need to add this method
            model.addAttribute("document", document);

            // Get all analyses for this document
            List<Analysis> analyses = analysisRepository.findByDocument(document);
            model.addAttribute("analyses", analyses);

            return "document-detail";
        } catch (RuntimeException e) {
            return "redirect:/documents";
        }
    }

    @GetMapping("/documents")
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());  // Add this method
        return "documents";
    }
}