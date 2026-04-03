package com.LMS.Pulse.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "usersubmissions")
public class UserSubmissions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @OneToMany(mappedBy = "userSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentAnswer> answers;

    @Column(length = 1024)
    private String userPhotoUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime submissionTimestamp;

    private Boolean LearningCompletion;
    private Boolean AssignmentCompletion;
    private Boolean CourseCompletion;
}