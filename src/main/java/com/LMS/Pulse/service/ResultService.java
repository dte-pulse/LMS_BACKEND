package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.AssignmentAnswerDto;
import com.LMS.Pulse.Dto.CourseResultDto;
import com.LMS.Pulse.Dto.UserResultDto;
import com.LMS.Pulse.model.AssignmentAnswer;
import com.LMS.Pulse.model.Course;
import com.LMS.Pulse.model.UserSubmissions;
import com.LMS.Pulse.repository.AssignmentAnswerRepository;
import com.LMS.Pulse.repository.CourseRepository;
import com.LMS.Pulse.repository.UserSubmissionsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final CourseRepository courseRepository;
    private final UserSubmissionsRepository userSubmissionsRepository;
    private final AssignmentAnswerRepository assignmentAnswerRepository;
    private final S3Service s3Service;

    public List<CourseResultDto> getAllCourseResults() {
        List<Course> courses = courseRepository.findAll();

        return courses.stream().map(course -> {
            List<UserSubmissions> submissions = userSubmissionsRepository.findByAssignment_Course_Id(course.getId());
            double averageScore = submissions.stream()
                    .flatMap(submission -> submission.getAnswers().stream())
                    .mapToDouble(AssignmentAnswer::getFinalScore)
                    .average()
                    .orElse(0.0);
            return new CourseResultDto(course.getId(), course.getCourseName(), averageScore);
        }).collect(Collectors.toList());
    }

    public List<UserResultDto> getUserResultsForCourse(Long courseId) {
        List<UserSubmissions> submissions = userSubmissionsRepository.findByAssignment_Course_Id(courseId);

        return submissions.stream()
                .collect(Collectors.groupingBy(UserSubmissions::getUserId))
                .entrySet().stream()
                .map(entry -> {
                    String userId = entry.getKey();
                    List<UserSubmissions> userSubmissions = entry.getValue();
                    String username = userSubmissions.get(0).getUsername();

                    double averageScore = userSubmissions.stream()
                            .flatMap(sub -> sub.getAnswers().stream())
                            .mapToDouble(AssignmentAnswer::getFinalScore)
                            .average()
                            .orElse(0.0);

                    return new UserResultDto(userId, username, averageScore);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AssignmentAnswerDto> getSubmissionsForReview(Long courseId, String userId) {
        List<UserSubmissions> submissions = userSubmissionsRepository.findByAssignment_Course_IdAndUserId(courseId, userId);
        return submissions.stream()
                .flatMap(submission -> submission.getAnswers().stream())
                .map(this::mapToAssignmentAnswerDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateFinalScore(Long answerId, double newScore) {
        AssignmentAnswer answer = assignmentAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("AssignmentAnswer not found with id: " + answerId));
        answer.setFinalScore((int) newScore);
        assignmentAnswerRepository.save(answer);
    }

    /**
     * Generates a fresh pre-signed URL for a specific answer’s video.
     * Called by the “View Video” button so the URL is always brand-new.
     */
    public String getFreshVideoUrl(Long answerId) {
        AssignmentAnswer answer = assignmentAnswerRepository.findById(answerId)
                .orElseThrow(() -> new EntityNotFoundException("AssignmentAnswer not found with id: " + answerId));
        return resolveVideoUrl(answer.getContentLink());
    }

    private AssignmentAnswerDto mapToAssignmentAnswerDto(AssignmentAnswer answer) {
        return new AssignmentAnswerDto(
                answer.getId(),
                answer.getQuestion(),
                answer.getUserAnswer(),
                resolveVideoUrl(answer.getContentLink()),
                answer.getGptScore(),
                answer.getFinalScore()
        );
    }

    /**
     * Resolves a stored contentLink to a fresh pre-signed S3 URL.
     *
     * Handles two formats:
     *   - New format : raw S3 key  (e.g. "1715000000000_answer.webm")
     *   - Legacy format: full pre-signed URL saved in old submissions
     *                    (URL may already be expired — we re-sign it)
     */
    private String resolveVideoUrl(String contentLink) {
        if (contentLink == null || contentLink.isBlank()) {
            return null;
        }
        if (contentLink.startsWith("https://")) {
            // Legacy row: extract the S3 object key from the URL path
            try {
                URL url = new URL(contentLink);
                String path = url.getPath();                 // e.g. "/1715000000000_video.webm"
                String key  = path.startsWith("/") ? path.substring(1) : path;
                return s3Service.generatePresignedUrl(key);
            } catch (Exception e) {
                // If parsing fails, return the stored value as a last-resort fallback
                return contentLink;
            }
        }
        // New format — contentLink is already just the S3 key
        return s3Service.generatePresignedUrl(contentLink);
    }
}