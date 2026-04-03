package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.UserSubmissionDto;
import com.LMS.Pulse.model.UserSubmissions;
import com.LMS.Pulse.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/assignment")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/submit-answer", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<String> handleFullSubmission(
            @RequestPart("submission") String userSubmissionDtoStr,
            @RequestPart("photo") MultipartFile photo,
            @RequestPart("videos") List<MultipartFile> videos) {

        try {
            UserSubmissionDto userSubmissionDto = objectMapper.readValue(userSubmissionDtoStr, UserSubmissionDto.class);

            if (videos.size() != userSubmissionDto.getAnswers().size()) {
                return new ResponseEntity<>("The number of videos does not match the number of answers.", HttpStatus.BAD_REQUEST);
            }

            UserSubmissions result = submissionService.processSubmission(userSubmissionDto, photo, videos);
            String courseName = result.getAssignment().getCourse().getCourseName();

            String responseMessage = String.format(
                    "Submission successful for Assignment ID %d in Course '%s'. Submission ID: %d",
                    result.getAssignment().getId(), courseName, result.getId()
            );
            return ResponseEntity.ok(responseMessage);

        } catch (IOException e) {
            return new ResponseEntity<>("Error processing request: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
