package com.zoha.docanalyser.controller;

import com.zoha.docanalyser.dto.ComparisonRequest;
import com.zoha.docanalyser.service.ComparisonService;
import com.zoha.docanalyser.repository.DocumentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/compare")
public class ComparisonController {

    private final ComparisonService comparisonService;
    private final DocumentRepository documentRepository;

    public ComparisonController(ComparisonService comparisonService, 
                                DocumentRepository documentRepository) {
        this.comparisonService = comparisonService;
        this.documentRepository = documentRepository;
    }

    @GetMapping
    public String showComparisonPage(Model model) {
        model.addAttribute("documents", documentRepository.findAll());
        model.addAttribute("comparisonRequest", new ComparisonRequest());
        return "compare";
    }

    @PostMapping
    public String compareDocuments(@ModelAttribute ComparisonRequest comparisonRequest,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        try {
            String result = comparisonService.compareDocuments(
                comparisonRequest.getDocumentId1(), 
                comparisonRequest.getDocumentId2()
            );
            
            redirectAttributes.addFlashAttribute("comparisonResult", result);
            redirectAttributes.addFlashAttribute("doc1Id", comparisonRequest.getDocumentId1());
            redirectAttributes.addFlashAttribute("doc2Id", comparisonRequest.getDocumentId2());
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Comparison failed: " + e.getMessage());
        }
        
        return "redirect:/compare";
    }
}