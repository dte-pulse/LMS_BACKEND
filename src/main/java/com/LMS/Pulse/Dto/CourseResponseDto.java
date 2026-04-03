package com.LMS.Pulse.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDto {

    private Long id;
    private String courseName;
    private String learningJotformName;
    private String assignmentJotformName;
    private String imageFileName;
    private Integer daysOfJoining;
    private String preRequisiteCourseName;

    // The conflicting 5-argument constructor has been removed.
}
