package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.CourseCompletionDto;
import com.LMS.Pulse.Dto.LearningCompletionRequestDto;
import com.LMS.Pulse.model.CourseCompletion;
import com.LMS.Pulse.model.User;
import com.LMS.Pulse.repository.CourseCompletionRepository;
import com.LMS.Pulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseCompletionService {

    private final CourseCompletionRepository completionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void markLearningAsCompleted(String username, LearningCompletionRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        CourseCompletion completion = completionRepository
                .findByUserAndCourseName(user, requestDto.getCourseName())
                .orElseGet(() -> new CourseCompletion(
                        user,
                        requestDto.getCourseName(),
                        requestDto.getLearningJotformName(),
                        requestDto.getAssignmentJotformName()
                ));

        completion.setLearningCompletion(true);
        completionRepository.save(completion);
    }

    public List<CourseCompletionDto> getUserCompletions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<CourseCompletion> completions = completionRepository.findByUser(user);

        // Convert the list of entities to a list of DTOs
        return completions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Helper method to map the entity to the DTO
    private CourseCompletionDto mapToDto(CourseCompletion completion) {
        return new CourseCompletionDto(
                completion.getId(),
                completion.getCourseName(),
                completion.getJotFormAssignmentName(),
                completion.getJotFormLearningName(),
                completion.getLearningCompletion(),
                completion.getAssignmentCompletion(),
                completion.getCertificateUrl()
        );
    }
}
