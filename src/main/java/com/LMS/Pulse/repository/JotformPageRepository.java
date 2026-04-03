package com.LMS.Pulse.repository;


import com.LMS.Pulse.model.JotformPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JotformPageRepository extends JpaRepository<JotformPage, Long> {
}