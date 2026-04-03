package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    // New method to find by our unique string identifier
    Optional<Assignment> findByAssignmentIdentifier(String assignmentIdentifier);

    List<Assignment> findByCourseId(Long courseId);
}
