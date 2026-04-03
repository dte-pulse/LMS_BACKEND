package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.CertificateResponseDto;
import com.LMS.Pulse.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    @GetMapping("/{username}")
    public ResponseEntity<List<CertificateResponseDto>> getCertificates(@PathVariable String username) {
        Long userId = certificateService.getUserIdByUsername(username);
        List<CertificateResponseDto> certificates = certificateService.getUserCertificates(userId);
        return ResponseEntity.ok(certificates);
    }
}
