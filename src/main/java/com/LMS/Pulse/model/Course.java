package com.LMS.Pulse.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = {"subjects"})
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String courseName;

    @ManyToMany(mappedBy = "courses")
    @JsonIgnoreProperties("courses")
    private Set<Subject> subjects = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "learning_jotform_id")
    @JsonIgnoreProperties({"pages"})
    private Jotform learningJotform;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_jotform_id")
    @JsonIgnoreProperties({"pages"})
    private Jotform assignmentJotform;

    @Lob
    private byte[] imageFile;
    private String imageFileName;

    @Lob
    private byte[] pdfFile;
    private String pdfFileName;

    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();

    private Integer daysOfJoining;
    private String preRequisiteCourseName;
}
