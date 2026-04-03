package com.LMS.Pulse.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "coursecompletion",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "courseName"})
        })
public class CourseCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String courseName;

    private String jotFormLearningName;
    private String jotFormAssignmentName;
    private Boolean learningCompletion = false;
    private Boolean assignmentCompletion = false;

    // Store the certificate S3 URL
    @Column(length = 1000)
    private String certificateUrl;

    public CourseCompletion(User user, String courseName, String jotFormLearningName, String jotFormAssignmentName) {
        this.user = user;
        this.courseName = courseName;
        this.jotFormLearningName = jotFormLearningName;
        this.jotFormAssignmentName = jotFormAssignmentName;
    }
}
