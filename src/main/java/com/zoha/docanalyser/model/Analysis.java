package com.zoha.docanalyser.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "analyses")
@Data
@NoArgsConstructor
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisType analysisType;
    
    @Column(length = 10000)
    private String result;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    // Helper constructor
    public Analysis(Document document, AnalysisType analysisType, String result) {
        this.document = document;
        this.analysisType = analysisType;
        this.result = result;
    }
}