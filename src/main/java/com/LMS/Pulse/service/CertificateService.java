package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.CertificateResponseDto;
import com.LMS.Pulse.model.CourseCompletion;
import com.LMS.Pulse.model.ByteArrayMultipartFile;
import com.LMS.Pulse.repository.CourseCompletionRepository;
import com.LMS.Pulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CourseCompletionRepository completionRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;
    private final S3Service s3Service;

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    public List<CertificateResponseDto> getUserCertificates(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CourseCompletion> completions = completionRepository.findByUserId(userId);

        return completions.stream()
                .collect(Collectors.groupingBy(CourseCompletion::getCourseName))
                .entrySet()
                .stream()
                .map(entry -> {
                    String courseName = entry.getKey();
                    List<CourseCompletion> courseRecords = entry.getValue();

                    boolean allLearningDone = courseRecords.stream()
                            .allMatch(c -> Boolean.TRUE.equals(c.getLearningCompletion()));
                    boolean allAssignmentsDone = courseRecords.stream()
                            .allMatch(c -> Boolean.TRUE.equals(c.getAssignmentCompletion()));

                    if (allLearningDone && allAssignmentsDone) {
                        CourseCompletion record = courseRecords.get(0);
                        String downloadUrl = record.getCertificateUrl();

                        try {
                            // Generate and upload PDF only if URL not present
                            if (downloadUrl == null) {
                                // Generate PDF bytes
                                byte[] pdfBytes = pdfService.generateCertificateBytes(userId, user.getUsername(), courseName);

                                // Create a unique file name: username_coursename_certificate.pdf
                                String fileName = user.getUsername() + "_" + courseName + "_certificate.pdf";

                                ByteArrayMultipartFile file = new ByteArrayMultipartFile(
                                        pdfBytes,
                                        fileName,  // original filename
                                        fileName,  // S3 key
                                        "application/pdf"
                                );

                                // Upload to S3 and get the key
                                String s3Key = s3Service.uploadFile(file);

                                // Generate pre-signed URL valid for 1 hour
                                downloadUrl = s3Service.generatePresignedUrl(s3Key);

                                // Save the URL in DB
                                record.setCertificateUrl(downloadUrl);
                                completionRepository.save(record);
                            }

                            return CertificateResponseDto.builder()
                                    .courseName(courseName)
                                    .completionStatus("COMPLETED")
                                    .issued(true)
                                    .issueDate(LocalDate.now().toString())
                                    .downloadUrl(downloadUrl)
                                    .message("Congratulations! You have completed this course.")
                                    .build();
                        } catch (Exception e) {
                            return CertificateResponseDto.builder()
                                    .courseName(courseName)
                                    .completionStatus("COMPLETED")
                                    .issued(false)
                                    .message("Failed to generate certificate: " + e.getMessage())
                                    .build();
                        }
                    } else {
                        return CertificateResponseDto.builder()
                                .courseName(courseName)
                                .completionStatus("IN_PROGRESS")
                                .issued(false)
                                .message("Course not yet completed.")
                                .build();
                    }
                })
                .collect(Collectors.toList());
    }
}
