package com.syslogmanager.application;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class ApplicationController {
    
    @GetMapping("/home")
    public String getHome() {
        return "Syslog Manager Application is running.";
    }
    
}