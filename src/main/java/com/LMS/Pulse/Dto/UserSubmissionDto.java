package com.LMS.Pulse.Dto;

import java.util.List;

public class UserSubmissionDto {
    private String jotformId;
    private String username;
    private String userId;
    private List<String> warnings;
    private List<AnswerSubmissionDto> answers;

    public String getJotformId() { return jotformId; }
    public void setJotformId(String jotformId) { this.jotformId = jotformId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    public List<AnswerSubmissionDto> getAnswers() { return answers; }
    public void setAnswers(List<AnswerSubmissionDto> answers) { this.answers = answers; }

    @Override
    public String toString() {
        return "UserSubmissionDto{" +
                "jotformId='" + jotformId + '\'' +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}