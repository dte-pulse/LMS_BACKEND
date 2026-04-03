package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.Jotform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JotformRepository extends JpaRepository<Jotform, Long> {
    Optional<Jotform> findByJotformName(String jotformName);

    Object findAllById(int i);
}
