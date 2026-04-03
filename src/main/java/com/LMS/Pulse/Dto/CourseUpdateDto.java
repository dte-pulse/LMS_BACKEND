package com.LMS.Pulse.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseUpdateDto {

    private String courseName;
    private String learningJotformName;
    private String assignmentJotformName;
    private MultipartFile imageFile;
    private MultipartFile pdfFile;
    private Integer daysOfJoining;
    private String preRequisiteCourseName;
}
