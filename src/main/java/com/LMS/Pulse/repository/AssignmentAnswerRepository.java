package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.AssignmentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentAnswerRepository extends JpaRepository<AssignmentAnswer, Long> {}
