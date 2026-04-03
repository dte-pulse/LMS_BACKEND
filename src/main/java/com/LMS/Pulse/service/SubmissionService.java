package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.AnswerSubmissionDto;
import com.LMS.Pulse.Dto.UserSubmissionDto;
import com.LMS.Pulse.model.*;
import com.LMS.Pulse.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final UserSubmissionsRepository userSubmissionsRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentAnswerRepository assignmentAnswerRepository;
    private final S3Service s3Service;
    private final GeminiService geminiService;
    private final UserRepository userRepository;
    private final CourseCompletionRepository courseCompletionRepository;

    /**
     * Processes a full user submission, including JSON data, an identity photo, and video answers.
     */
    @Transactional
    public UserSubmissions processSubmission(UserSubmissionDto dto, MultipartFile identityPhoto, List<MultipartFile> videoFiles) throws IOException {

        // Fetch userId from username
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + dto.getUsername()));

        // Find the assignment by jotformId
        String assignmentIdentifier = dto.getJotformId();
        Assignment assignment = assignmentRepository.findByAssignmentIdentifier(assignmentIdentifier)
                .orElseThrow(() -> new EntityNotFoundException("No assignment found with identifier: " + assignmentIdentifier));

        // 1. Upload the identity photo to S3
        String photoKey = s3Service.uploadFile(identityPhoto);
        String photoUrl = s3Service.generatePresignedUrl(photoKey);

        // 2. Create the main UserSubmissions record
        UserSubmissions submission = new UserSubmissions();
        submission.setUsername(dto.getUsername());
        submission.setUserId(user.getId().toString());
        submission.setAssignment(assignment);
        submission.setUserPhotoUrl(photoUrl);
        submission.setAnswers(new ArrayList<>());

        UserSubmissions savedSubmission = userSubmissionsRepository.save(submission);

        // 3. Process each answer
        for (int i = 0; i < dto.getAnswers().size(); i++) {
            AnswerSubmissionDto answerDto = dto.getAnswers().get(i);
            MultipartFile videoFile = videoFiles.get(i);

            // Upload video to S3
            String videoKey = s3Service.uploadFile(videoFile);
            String videoUrl = s3Service.generatePresignedUrl(videoKey);

            // Evaluate the transcript with Gemini
            int score = geminiService.getScoreForAnswer(answerDto.getQuestionText(), answerDto.getTranscript());

            // Save the answer
            AssignmentAnswer answer = new AssignmentAnswer();
            answer.setUserSubmission(savedSubmission);
            answer.setQuestion(answerDto.getQuestionText());
            answer.setUserAnswer(answerDto.getTranscript());
            answer.setContentLink(videoUrl);
            answer.setGptScore(score);
            answer.setFinalScore(score);

            savedSubmission.getAnswers().add(assignmentAnswerRepository.save(answer));
        }

        // 4. Update CourseCompletion table: set assignmentCompletion = true
        String courseName = assignment.getCourse().getCourseName();
        CourseCompletion courseCompletion = courseCompletionRepository.findByUserAndCourseName(user, courseName)
                .orElseThrow(() -> new EntityNotFoundException("CourseCompletion record not found for user and course: " + courseName));

        courseCompletion.setAssignmentCompletion(true);
        courseCompletionRepository.save(courseCompletion);

        return savedSubmission;
    }
}
