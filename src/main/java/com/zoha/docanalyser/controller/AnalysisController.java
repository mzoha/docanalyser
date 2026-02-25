package com.zoha.docanalyser.controller;

import com.zoha.docanalyser.model.Analysis;
import com.zoha.docanalyser.model.AnalysisType;
import com.zoha.docanalyser.service.AnalysisService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/{documentId}")
    public String analyzeDocument(@PathVariable Long documentId,
                                  @RequestParam AnalysisType type,
                                  RedirectAttributes redirectAttributes) {
        try {
            Analysis analysis = analysisService.analyzeDocument(documentId, type);
            redirectAttributes.addFlashAttribute("success", 
                type + " analysis completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Analysis failed: " + e.getMessage());
        }
        
        return "redirect:/documents/" + documentId;
    }
}