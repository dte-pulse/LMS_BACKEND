package com.LMS.Pulse.controller;

import com.LMS.Pulse.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String key = s3Service.uploadFile(file);
            return ResponseEntity.ok(key);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> download(@PathVariable String key) {
        return ResponseEntity.ok("Download endpoint not fully implemented in S3Service.");
    }

    @GetMapping("/generate-presigned-url/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String key) {
        String url = s3Service.generatePresignedUrl(key);
        return ResponseEntity.ok(url);
    }
}