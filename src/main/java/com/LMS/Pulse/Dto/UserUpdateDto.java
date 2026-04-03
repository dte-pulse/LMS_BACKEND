package com.LMS.Pulse.Dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfJoining;
}