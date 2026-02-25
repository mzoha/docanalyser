package com.zoha.docanalyser.repository;

import com.zoha.docanalyser.model.Analysis;
import com.zoha.docanalyser.model.Document;
import com.zoha.docanalyser.model.AnalysisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByDocument(Document document);
    Optional<Analysis> findByDocumentAndAnalysisType(Document document, AnalysisType analysisType);
}