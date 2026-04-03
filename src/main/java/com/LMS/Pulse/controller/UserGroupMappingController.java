package com.LMS.Pulse.controller;
import com.LMS.Pulse.model.UserGroupMapping;
import com.LMS.Pulse.service.UserGroupMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/user-mappings")
public class UserGroupMappingController {

    @Autowired
    private UserGroupMappingService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserGroupMapping> createMapping(@RequestBody UserGroupMapping mapping) {
        return ResponseEntity.ok(service.mapUserToGroup(mapping));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserGroupMapping>> getAllMappings() {
        return ResponseEntity.ok(service.getAllMappings());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMapping(@PathVariable Long id) {
        try {
            service.deleteMapping(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}