package com.LMS.Pulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jotform_elements")
public class JotformElement {
    @Id
    private Long id;

    private String tagName;

    private String elementName;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    @JsonBackReference
    private JotformPage page;
}