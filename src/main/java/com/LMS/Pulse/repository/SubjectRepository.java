package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectName(String subjectName);

    @Query("SELECT s FROM Subject s LEFT JOIN FETCH s.courses")
    List<Subject> findAllWithCourses();
}