package com.LMS.Pulse.Dto;

import com.LMS.Pulse.model.Role;
import com.LMS.Pulse.model.UserGroupMapping;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
public class UserDetailsDto {
 private String username;
 private String email;
 private LocalDate dateOfJoining;
 private String groupName;
 private Role role;

 // --- FIX: This constructor now correctly assigns values ---
 public UserDetailsDto(String username, String email, LocalDate dateOfJoining, Optional<UserGroupMapping> groupMappingOpt, Role role) {
  this.username = username;
  this.email = email;
  this.dateOfJoining = dateOfJoining;
  // Safely extract the group name from the Optional, or set to null if not present
  this.groupName = groupMappingOpt.map(UserGroupMapping::getGroupName).orElse(null);
  this.role = role;
 }
}
