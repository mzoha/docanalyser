package com.zoha.docanalyser.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(name = "file_path", nullable = false)
    private String filePath;   // path where file is stored locally

    @Column(name = "upload_time")
    private LocalDateTime uploadTime = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status = ProcessingStatus.UPLOADED;

    // later we'll add extracted_text, but not yet
}

enum ProcessingStatus {
    UPLOADED, PROCESSING, COMPLETED, FAILED
}