package com.LMS.Pulse.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseCompletionDto {
    private Long id;
    private String courseName;
    private String jotFormAssignmentName;
    private String jotFormLearningName;
    private Boolean learningCompletion;
    private Boolean assignmentCompletion;
    private String certificateUrl;
}
