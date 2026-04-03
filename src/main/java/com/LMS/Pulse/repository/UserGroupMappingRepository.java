package com.LMS.Pulse.repository;// package com.LMS.Pulse.repository;

import com.LMS.Pulse.model.UserGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupMappingRepository extends JpaRepository<UserGroupMapping, Long> {
    // You can add custom query methods here if needed
    Optional<UserGroupMapping> findByEmail(String email);
}
