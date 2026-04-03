package com.LMS.Pulse.controller;

import com.LMS.Pulse.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/ask")
    @PreAuthorize("hasRole('ADMIN')")
    public String askGeminiApi(@RequestBody String prompt) {
        return geminiService.askGemini(prompt);
    }
}