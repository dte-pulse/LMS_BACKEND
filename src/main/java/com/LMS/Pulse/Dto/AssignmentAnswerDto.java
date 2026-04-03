package com.LMS.Pulse.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignmentAnswerDto {
    private Long id;
    private String question;
    private String userAnswer;
    private String contentLink;
    private int gptScore;
    private double finalScore;
}