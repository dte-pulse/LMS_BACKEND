package com.LMS.Pulse.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResultDto {
    private Long id;
    private String courseName;
    private double averageScore;
}