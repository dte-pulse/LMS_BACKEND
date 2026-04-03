package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.CourseCompletion;
import com.LMS.Pulse.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCompletionRepository extends JpaRepository<CourseCompletion, Long> {
    Optional<CourseCompletion> findByUserAndCourseName(User user, String courseName);
    List<CourseCompletion> findByUserId(Long userId);

    List<CourseCompletion> findByUser(User user);
}
