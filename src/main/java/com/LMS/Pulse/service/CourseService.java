package com.LMS.Pulse.service;

import com.LMS.Pulse.Dto.CourseResponseDto;
import com.LMS.Pulse.Dto.CourseUpdateDto;
import com.LMS.Pulse.model.Assignment;
import com.LMS.Pulse.model.Course;
import com.LMS.Pulse.model.Jotform;
import com.LMS.Pulse.repository.AssignmentRepository;
import com.LMS.Pulse.repository.CourseRepository;
import com.LMS.Pulse.repository.JotformRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final JotformRepository jotformRepository;
    private final AssignmentRepository assignmentRepository;

    public List<CourseResponseDto> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        return courses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<String> getCourseNames() {
        return courseRepository.findDistinctCourseNames();
    }

    @Transactional
    public Course createLearningCourse(String courseName, String jotformName,
                                       MultipartFile imageFile, MultipartFile pdfFile,
                                       Integer daysOfJoining, String preRequisiteCourseName) throws IOException {
        Jotform learningForm = jotformRepository.findByJotformName(jotformName)
                .orElseThrow(() -> new RuntimeException("Jotform not found with name: " + jotformName));

        if (courseRepository.findByCourseName(courseName).isPresent()) {
            throw new IllegalStateException("A course with the name '" + courseName + "' already exists.");
        }

        Course course = new Course();
        course.setCourseName(courseName);
        course.setLearningJotform(learningForm);
        course.setDaysOfJoining(daysOfJoining);
        course.setPreRequisiteCourseName(preRequisiteCourseName);

        if (imageFile != null && !imageFile.isEmpty()) {
            course.setImageFile(imageFile.getBytes());
            course.setImageFileName(imageFile.getOriginalFilename());
        }
        if (pdfFile != null && !pdfFile.isEmpty()) {
            course.setPdfFile(pdfFile.getBytes());
            course.setPdfFileName(pdfFile.getOriginalFilename());
        }

        return courseRepository.save(course);
    }

    @Transactional
    public Course mapAssignmentCourse(String courseName, String jotformName) {
        Course course = courseRepository.findByCourseName(courseName)
                .orElseThrow(() -> new RuntimeException("Course not found with name: " + courseName));

        Jotform assignmentJotform = jotformRepository.findByJotformName(jotformName)
                .orElseThrow(() -> new RuntimeException("Jotform not found with name: " + jotformName));

        course.setAssignmentJotform(assignmentJotform);
        Course updatedCourse = courseRepository.save(course);

        Assignment assignment = assignmentRepository.findByAssignmentIdentifier(jotformName)
                .orElse(new Assignment());

        assignment.setCourse(updatedCourse);
        assignment.setAssignmentIdentifier(jotformName);
        assignment.setTitle(course.getCourseName() + " - " + jotformName);

        assignmentRepository.save(assignment);

        return updatedCourse;
    }

    @Transactional
    public CourseResponseDto updateCourse(Long id, CourseUpdateDto dto) throws IOException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        // Update course name
        if (StringUtils.hasText(dto.getCourseName()) && !Objects.equals(dto.getCourseName(), course.getCourseName())) {
            course.setCourseName(dto.getCourseName());
        }

        // Update learning Jotform
        if (StringUtils.hasText(dto.getLearningJotformName())) {
            Jotform learningJotform = jotformRepository.findByJotformName(dto.getLearningJotformName())
                    .orElseThrow(() -> new RuntimeException("Learning Jotform not found: " + dto.getLearningJotformName()));
            course.setLearningJotform(learningJotform);
        }

        // Update assignment Jotform and synchronize with Assignment table
        if (StringUtils.hasText(dto.getAssignmentJotformName())) {
            Jotform assignmentJotform = jotformRepository.findByJotformName(dto.getAssignmentJotformName())
                    .orElseThrow(() -> new RuntimeException("Assignment Jotform not found: " + dto.getAssignmentJotformName()));
            course.setAssignmentJotform(assignmentJotform);

            // Check if an Assignment already exists for this jotformName
            Assignment assignment = assignmentRepository.findByAssignmentIdentifier(dto.getAssignmentJotformName())
                    .orElse(new Assignment());

            // Update or create the Assignment
            assignment.setCourse(course);
            assignment.setAssignmentIdentifier(dto.getAssignmentJotformName());
            assignment.setTitle(course.getCourseName() + " - " + dto.getAssignmentJotformName());
            assignmentRepository.save(assignment);
        } else if (course.getAssignmentJotform() != null && dto.getAssignmentJotformName() == null) {
            // If assignmentJotformName is cleared, remove the Assignment link (optional)
            course.setAssignmentJotform(null);
            Assignment existingAssignment = assignmentRepository.findByCourseId(course.getId())
                    .stream().findFirst().orElse(null);
            if (existingAssignment != null) {
                assignmentRepository.delete(existingAssignment);
            }
        }

        // Update image file
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            course.setImageFile(dto.getImageFile().getBytes());
            course.setImageFileName(dto.getImageFile().getOriginalFilename());
        }

        // Update PDF file
        if (dto.getPdfFile() != null && !dto.getPdfFile().isEmpty()) {
            course.setPdfFile(dto.getPdfFile().getBytes());
            course.setPdfFileName(dto.getPdfFile().getOriginalFilename());
        }

        // Update daysOfJoining
        if (dto.getDaysOfJoining() != null) {
            course.setDaysOfJoining(dto.getDaysOfJoining());
        }

        // Update preRequisiteCourseName
        if (dto.getPreRequisiteCourseName() != null) {
            course.setPreRequisiteCourseName(dto.getPreRequisiteCourseName().isEmpty() ? null : dto.getPreRequisiteCourseName());
        }

        course.setUpdatedDate(LocalDateTime.now());
        Course updatedCourse = courseRepository.save(course);
        return mapToDto(updatedCourse);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    private CourseResponseDto mapToDto(Course course) {
        String learningJotformName = course.getLearningJotform() != null ? course.getLearningJotform().getJotformName() : null;
        String assignmentJotformName = course.getAssignmentJotform() != null ? course.getAssignmentJotform().getJotformName() : null;

        return new CourseResponseDto(
                course.getId(),
                course.getCourseName(),
                learningJotformName,
                assignmentJotformName,
                course.getImageFileName(),
                course.getDaysOfJoining(),
                course.getPreRequisiteCourseName()
        );
    }
}