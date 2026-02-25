package com.zoha.docanalyser.controller;

import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String index() {
        return "upload";  // we'll create upload.html
    }

    @PostMapping("/upload")
    public String uploadDocument(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            Document savedDocument = documentService.uploadDocument(file);
            redirectAttributes.addFlashAttribute("message",
                    "File uploaded successfully! Document ID: " + savedDocument.getId());
            return "redirect:/documents/" + savedDocument.getId();
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "upload";
        } catch (IOException e) {
            model.addAttribute("error", "Failed to store file: " + e.getMessage());
            return "upload";
        }
    }
}