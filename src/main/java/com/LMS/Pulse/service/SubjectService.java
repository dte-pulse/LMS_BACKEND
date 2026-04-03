package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.CourseResponseDto;
import com.LMS.Pulse.Dto.SubjectRequestDto;
import com.LMS.Pulse.Dto.SubjectResponseDto;
import com.LMS.Pulse.model.Course;
import com.LMS.Pulse.model.Subject;
import com.LMS.Pulse.repository.CourseRepository;
import com.LMS.Pulse.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;

    public List<SubjectResponseDto> getAllSubjects() {
        return subjectRepository.findAllWithCourses().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubjectResponseDto createSubject(SubjectRequestDto requestDto) {
        Subject subject = new Subject();
        subject.setGroupName(requestDto.getGroupName());
        subject.setSubjectName(requestDto.getSubjectName());

        Set<Long> courseIds = requestDto.getCourseIds();
        if (courseIds != null && !courseIds.isEmpty()) {
            List<Course> courses = courseRepository.findAllById(courseIds);
            subject.setCourses(courses.stream().collect(Collectors.toSet()));
        } else {
            subject.setCourses(Collections.emptySet());
        }

        Subject savedSubject = subjectRepository.save(subject);
        return mapToDto(savedSubject);
    }

    @Transactional
    public SubjectResponseDto updateSubject(Long id, SubjectRequestDto requestDto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        subject.setSubjectName(requestDto.getSubjectName());
        subject.setGroupName(requestDto.getGroupName());

        Set<Long> courseIds = requestDto.getCourseIds();
        if (courseIds != null) {
            List<Course> courses = courseRepository.findAllById(courseIds);
            subject.setCourses(courses.stream().collect(Collectors.toSet()));
        }

        Subject updatedSubject = subjectRepository.save(subject);
        return mapToDto(updatedSubject);
    }

    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    private SubjectResponseDto mapToDto(Subject subject) {
        List<CourseResponseDto> courseDtos = subject.getCourses().stream()
                .map(course -> new CourseResponseDto(
                        course.getId(),
                        course.getCourseName(),
                        course.getLearningJotform() != null ? course.getLearningJotform().getJotformName() : null,
                        course.getAssignmentJotform() != null ? course.getAssignmentJotform().getJotformName() : null,
                        course.getImageFileName(),
                        course.getDaysOfJoining(),
                        course.getPreRequisiteCourseName()
                ))
                .collect(Collectors.toList());

        return new SubjectResponseDto(
                subject.getId(),
                subject.getSubjectName(),
                subject.getGroupName(),
                courseDtos
        );
    }
}
