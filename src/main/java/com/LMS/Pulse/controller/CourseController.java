package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.CourseResponseDto;
import com.LMS.Pulse.Dto.CourseUpdateDto;
import com.LMS.Pulse.Dto.MapAssignmentRequest;
import com.LMS.Pulse.model.Course;
import com.LMS.Pulse.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN')")
    public ResponseEntity<List<CourseResponseDto>> getAllCourses() {
        List<CourseResponseDto> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/names")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<String>> getCourseNames() {
        List<String> courseNames = courseService.getCourseNames();
        return ResponseEntity.ok(courseNames);
    }

    @PostMapping("/learning")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> mapLearningJotform(
            @RequestParam("courseName") String courseName,
            @RequestParam("jotformName") String jotformName,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "pdfFile", required = false) MultipartFile pdfFile,
            @RequestParam(value = "daysOfJoining", required = false) Integer daysOfJoining,
            @RequestParam(value = "preRequisiteCourseName", required = false) String preRequisiteCourseName
    ) throws IOException {
        Course course = courseService.createLearningCourse(courseName, jotformName, imageFile, pdfFile, daysOfJoining, preRequisiteCourseName);
        return ResponseEntity.ok(course);
    }

    @PostMapping("/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> mapAssignmentJotform(
            @RequestBody MapAssignmentRequest request
    ) {
        Course updatedCourse = courseService.mapAssignmentCourse(
                request.getCourseName(),
                request.getJotformName()
        );
        return ResponseEntity.ok(updatedCourse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDto> updateCourse(
            @PathVariable Long id,
            @ModelAttribute CourseUpdateDto courseUpdateDto
    ) throws IOException {
        CourseResponseDto updatedCourse = courseService.updateCourse(id, courseUpdateDto);
        return ResponseEntity.ok(updatedCourse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}