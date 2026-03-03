package com.zoha.docanalyser.dto;

import lombok.Data;

@Data
public class ComparisonRequest {
    private Long documentId1;
    private Long documentId2;
}