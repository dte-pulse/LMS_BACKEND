package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.AssignmentAnswerDto;
import com.LMS.Pulse.Dto.CourseResultDto;
import com.LMS.Pulse.Dto.UpdateScoreDto;
import com.LMS.Pulse.Dto.UserResultDto;
import com.LMS.Pulse.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<CourseResultDto> getAllCourseResults() {
        return resultService.getAllCourseResults();
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResultDto> getUserResults(@PathVariable Long courseId) {
        return resultService.getUserResultsForCourse(courseId);
    }

    @GetMapping("/submissions/{courseId}/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssignmentAnswerDto>> getSubmissionsForReview(
            @PathVariable Long courseId,
            @PathVariable String userId) {
        List<AssignmentAnswerDto> submissionDetails = resultService.getSubmissionsForReview(courseId, userId);
        return ResponseEntity.ok(submissionDetails);
    }

    @PutMapping("/answer/{answerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateFinalScore(
            @PathVariable Long answerId,
            @RequestBody UpdateScoreDto updateScoreDto) {
        resultService.updateFinalScore(answerId, updateScoreDto.getFinalScore());
        return ResponseEntity.ok().build();
    }
}