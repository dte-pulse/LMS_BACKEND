package com.LMS.Pulse.service;

import com.LMS.Pulse.model.Role;
import com.LMS.Pulse.model.User;
import com.LMS.Pulse.model.UserGroupMapping;
import com.LMS.Pulse.repository.UserGroupMappingRepository;
import com.LMS.Pulse.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserGroupMappingService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserGroupMappingRepository repository;

    public UserGroupMapping mapUserToGroup(UserGroupMapping mapping) {
        String useremail = mapping.getEmail();
        System.out.println("Mapping email: " + useremail);

        Optional<User> userdetails = userRepo.findByEmail(useremail);

        if (userdetails.isPresent()) {
            User user = userdetails.get();
            System.out.println("User found: " + user.toString());

            user.setRole(Role.EMPLOYEE); // Use enum directly
            userRepo.save(user); // Persist the updated user
        } else {
            System.out.println("User not found for email: " + useremail);
            // Optionally throw an exception or handle the missing user case
        }

        return repository.save(mapping);
    }

    public List<UserGroupMapping> getAllMappings() {
        return repository.findAll();
    }

    public void deleteMapping(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Mapping not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
