package com.zoha.docanalyser.model;

public enum ProcessingStatus {
    UPLOADED,      // File uploaded but not yet processed
    PROCESSING,    // Currently extracting text
    COMPLETED,     // Text extraction successful
    FAILED         // Text extraction failed
}