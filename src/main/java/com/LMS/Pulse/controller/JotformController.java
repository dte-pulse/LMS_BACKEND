package com.LMS.Pulse.controller;

import com.LMS.Pulse.model.Jotform;
import com.LMS.Pulse.service.JotformService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jotforms")
public class JotformController {

    @Autowired
    private JotformService jotformService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public List<Jotform> getAllJotforms() {
        return jotformService.getAllJotforms();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Jotform> createJotform(@RequestBody Map<String, Object> data) {
        Jotform savedJotform = jotformService.saveJotform(data);
        return ResponseEntity.ok(savedJotform);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteJotform(@PathVariable Long id) {
        try {
            jotformService.deleteJotformById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/names")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getAllJotformNames() {
        List<String> names = jotformService.getAllJotformNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/react")
    @PreAuthorize("hasRole('ADMIN')")
    public Jotform getreact() {
        return jotformService.getreact();
    }
}