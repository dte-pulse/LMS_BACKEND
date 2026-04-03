package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.SubjectRequestDto;
import com.LMS.Pulse.Dto.SubjectResponseDto;
import com.LMS.Pulse.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectResponseDto> createSubject(@RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto subject = subjectService.createSubject(requestDto);
        return ResponseEntity.ok(subject);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<Map<String, List<SubjectResponseDto>>> getAllSubjectsGrouped() {
        List<SubjectResponseDto> allSubjects = subjectService.getAllSubjects();
        Map<String, List<SubjectResponseDto>> groupedSubjects = allSubjects.stream()
                .collect(Collectors.groupingBy(SubjectResponseDto::getGroupName));
        return ResponseEntity.ok(groupedSubjects);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectResponseDto> updateSubject(@PathVariable Long id, @RequestBody SubjectRequestDto requestDto) {
        SubjectResponseDto updatedSubject = subjectService.updateSubject(id, requestDto);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }
}
