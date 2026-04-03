package com.LMS.Pulse.Dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateResponseDto {
    private String courseName;
    private String completionStatus; // COMPLETED / IN_PROGRESS
    private boolean issued;
    private String issueDate;
    private String downloadUrl;
    private String message;
}
