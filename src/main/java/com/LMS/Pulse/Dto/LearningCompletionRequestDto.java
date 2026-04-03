package com.LMS.Pulse.Dto;

import lombok.Data;

@Data
public class LearningCompletionRequestDto {
    private String courseName;
    private String learningJotformName;
    private String assignmentJotformName;
}
