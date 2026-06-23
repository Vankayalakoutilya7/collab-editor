package com.koutilya.collabeditor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String home() {
        return "Collab Editor Backend is running 🚀";
    }
}