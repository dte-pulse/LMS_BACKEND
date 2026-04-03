package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.UserSubmissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserSubmissionsRepository extends JpaRepository<UserSubmissions, Long> {
    List<UserSubmissions> findByAssignment_Course_Id(Long courseId);

    // **NEW METHOD** for getting specific user submissions for a course
    List<UserSubmissions> findByAssignment_Course_IdAndUserId(Long courseId, String userId);
}
