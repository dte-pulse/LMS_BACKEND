package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.CourseCompletionDto;
import com.LMS.Pulse.Dto.LearningCompletionRequestDto;
import com.LMS.Pulse.service.CourseCompletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/completions")
@RequiredArgsConstructor
public class CourseCompletionController {

    private final CourseCompletionService completionService;

    @PostMapping("/learning")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> completeLearning(@RequestBody LearningCompletionRequestDto requestDto, Authentication authentication) {
        String username = authentication.getName();
        completionService.markLearningAsCompleted(username, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CourseCompletionDto>> getUserCompletions(Authentication authentication) {
        String username = authentication.getName();
        List<CourseCompletionDto> completions = completionService.getUserCompletions(username);
        return ResponseEntity.ok(completions);
    }
}
