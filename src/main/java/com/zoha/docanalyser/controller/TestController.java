package com.zoha.docanalyser.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @Value("${test.message:Not found}")
    private String testMessage;
    
    @GetMapping("/test-props")
    public String testProps() {
        return "Test message: " + testMessage;
    }
}