package com.example.demo.unit.controllers;

import com.example.demo.TeacherPanelController;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherPanelControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherPanelController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testTeacherDashboard_ValidUser() {
        User user = new User();
        user.setId(101L);
        user.setUsername("teacher1");
        when(userRepository.findByUsername("teacher1")).thenReturn(user);

        Principal principal = () -> "teacher1";

        String view = controller.teacherDashboard(model, principal);

        verify(model).addAttribute("teacherId", 101L);
        verify(model).addAttribute("username", "teacher1");
        verify(model).addAttribute("title", "Teacher Panel");
        // Check that the returned view name is correct.
        assertEquals("teacherDashboard", view);
    }
    @Test
    void testTeacherDashboard_UserNotFound() {
        when(userRepository.findByUsername("missingTeacher")).thenReturn(null);
        Principal principal = () -> "missingTeacher";

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                controller.teacherDashboard(model, principal));
        assertEquals("User not found", exception.getMessage());
    }
}
