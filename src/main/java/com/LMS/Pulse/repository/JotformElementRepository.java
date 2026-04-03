package com.LMS.Pulse.repository;


import com.LMS.Pulse.model.JotformElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JotformElementRepository extends JpaRepository<JotformElement, Long> {
}