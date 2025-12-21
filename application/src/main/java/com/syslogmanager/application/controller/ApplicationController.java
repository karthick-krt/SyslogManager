package com.syslogmanager.application.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {
    
    @GetMapping("/home")
    public String home() {
        return "Syslog Manager Application is running.";
    }
    
}