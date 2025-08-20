package com.example.demo.unit.controllers;

import com.example.demo.AdminClassStudentsController;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminClassStudentsControllerTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminClassStudentsController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testViewClassStudents_ClassExists() {
        Long classId = 1L;
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(classId);
        schoolClass.setName("Math 101");

        ClassSignUp signup = new ClassSignUp();
        signup.setId(10L);
        signup.setUserId(100L);
        signup.setSchoolClassId(classId);
        signup.setStatus("APPROVED");
        signup.setCreatedDate(LocalDateTime.now());

        User student = new User();
        student.setId(100L);
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setRole("USER");

        User availableStudent = new User();
        availableStudent.setId(101L);
        availableStudent.setFirstName("Jane");
        availableStudent.setLastName("Smith");
        availableStudent.setRole("USER");

        // Stub raw returns (no Optional)
        when(schoolClassRepository.findById(classId))
                .thenReturn(schoolClass);
        when(classSignUpRepository.findBySchoolClassId(classId))
                .thenReturn(List.of(signup));
        when(userRepository.findById(100L))
                .thenReturn(Optional.of(student));
        when(userRepository.findAll())
                .thenReturn(Arrays.asList(student, availableStudent));

        String viewName = controller.viewClassStudents(classId, model);

        assertEquals("adminClassStudents", viewName);
        verify(model).addAttribute("schoolClass", schoolClass);
        verify(model).addAttribute(eq("signUpDetails"), any(List.class));
    }

    @Test
    void testViewClassStudents_ClassNotFound() {
        Long classId = 999L;
        // Stub to return null when not found
        when(schoolClassRepository.findById(classId))
                .thenReturn(null);

        String viewName = controller.viewClassStudents(classId, model);

        assertEquals("redirect:/admin/classes?error=classNotFound", viewName);
        verify(model, never()).addAttribute(eq("schoolClass"), any());
        verify(model, never()).addAttribute(eq("signUpDetails"), any());
    }

    @Test
    void testRemoveStudent() {
        Long classId = 1L;
        Long signUpId = 10L;

        String viewName = controller.removeStudent(classId, signUpId);

        verify(classSignUpRepository, times(1)).delete(signUpId);
        assertEquals("redirect:/admin/classes/1/students", viewName);
    }

    @Test
    void testAddStudent_EnrollmentExists() {
        Long classId = 1L;
        Long studentId = 100L;

        when(classSignUpRepository.findBySchoolClassIdAndUserId(classId, studentId))
                .thenReturn(Optional.of(new ClassSignUp()));

        String viewName = controller.addStudent(classId, studentId);

        verify(classSignUpRepository, never()).save(any(ClassSignUp.class));
        assertEquals("redirect:/admin/classes/1/students", viewName);
    }

    @Test
    void testAddStudent_NewEnrollment() {
        Long classId = 1L;
        Long studentId = 101L;

        when(classSignUpRepository.findBySchoolClassIdAndUserId(classId, studentId))
                .thenReturn(Optional.empty());

        String viewName = controller.addStudent(classId, studentId);

        verify(classSignUpRepository, times(1)).save(any(ClassSignUp.class));
        assertEquals("redirect:/admin/classes/1/students", viewName);
    }
}