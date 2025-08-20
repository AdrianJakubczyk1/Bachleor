package com.example.demo.unit.controllers;

import com.example.demo.TeacherAvailableClassesController;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherAvailableClassesControllerUnitTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherAvailableClassesController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAvailableClasses() {
        SchoolClass class1 = new SchoolClass();
        class1.setId(1L);
        class1.setName("Math 101");
        class1.setTeacherId(null);
        SchoolClass class2 = new SchoolClass();
        class2.setId(2L);
        class2.setName("History 101");
        class2.setTeacherId(null);
        List<SchoolClass> availableClasses = Arrays.asList(class1, class2);
        when(schoolClassRepository.findByTeacherIdIsNull()).thenReturn(availableClasses);
        String view = controller.listAvailableClasses(model, () -> "anyUser");
        verify(model).addAttribute("availableClasses", availableClasses);
        assertEquals("teacherAvailableClasses", view);
    }

    @Test
    void testAssignSelfToClass_TeacherNotFound() {
        Long classId = 10L;
        Principal principal = () -> "teacherX";
        when(userRepository.findByUsername("teacherX")).thenReturn(null);

        String view = controller.assignSelfToClass(classId, principal);

        assertEquals("redirect:/login", view);
        verify(userRepository).findByUsername("teacherX");
        verifyNoInteractions(schoolClassRepository);
    }

    @Test
    void testAssignSelfToClass_ClassNotFound() {
        Long classId = 20L;
        Principal principal = () -> "teacherY";
        User teacher = new User();
        teacher.setId(101L);
        teacher.setUsername("teacherY");

        when(userRepository.findByUsername("teacherY")).thenReturn(teacher);
        when(schoolClassRepository.findById(classId)).thenReturn(null);

        String view = controller.assignSelfToClass(classId, principal);

        assertEquals("redirect:/teacher/availableClasses", view);
        verify(userRepository).findByUsername("teacherY");
        verify(schoolClassRepository).findById(classId);
        verify(schoolClassRepository, never()).save(any(SchoolClass.class));
    }

    @Test
    void testAssignSelfToClass_Success() {
        Long classId = 30L;
        Principal principal = () -> "teacherZ";
        User teacher = new User();
        teacher.setId(202L);
        teacher.setUsername("teacherZ");

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(classId);
        schoolClass.setName("Science 101");
        schoolClass.setTeacherId(null);

        when(userRepository.findByUsername("teacherZ")).thenReturn(teacher);
        when(schoolClassRepository.findById(classId)).thenReturn(schoolClass);

        String view = controller.assignSelfToClass(classId, principal);
        assertEquals(202L, schoolClass.getTeacherId());
        verify(schoolClassRepository).save(schoolClass);
        assertEquals("redirect:/teacher/availableClasses", view);
    }
}