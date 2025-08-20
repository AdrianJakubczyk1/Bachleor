package com.example.demo.unit.controllers;
import com.example.demo.AdminClassesController;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class AdminClassesControllerTest {
    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminClassesController adminClassesController;

    @Test
    void testListClasses() {
        when(schoolClassRepository.findAll()).thenReturn(List.of(new SchoolClass()));

        String viewName = adminClassesController.listClasses(model);

        verify(schoolClassRepository).findAll();
        verify(model).addAttribute(eq("classes"), anyList());
        assertEquals("adminClasses", viewName);
    }


    @Test
    void testShowAssignTeacherFormClassNotFound() {
        // Stub the repo to return null when not found
        when(schoolClassRepository.findById(anyLong()))
                .thenReturn(null);

        String viewName = adminClassesController.showAssignTeacherForm(1L, model);

        // Should redirect back to the list
        assertEquals("redirect:/admin/classes", viewName);
        // And never attempt to populate the model
        verify(model, never()).addAttribute(eq("schoolClass"), any());
        verify(model, never()).addAttribute(eq("teachers"), any());
    }

    @Test
    void testAssignTeacherSuccess() {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(1L);

        // Stub to return the raw entity
        when(schoolClassRepository.findById(1L))
                .thenReturn(schoolClass);

        String viewName = adminClassesController.assignTeacher(1L, 2L);

        verify(schoolClassRepository).save(any(SchoolClass.class));
        assertEquals(2L, schoolClass.getTeacherId());
        assertEquals("redirect:/admin/classes", viewName);
    }

    @Test
    void testAssignTeacherClassNotFound() {
        // Stub to return null when not found
        when(schoolClassRepository.findById(anyLong()))
                .thenReturn(null);

        String viewName = adminClassesController.assignTeacher(1L, 2L);

        verify(schoolClassRepository, never()).save(any(SchoolClass.class));
        assertEquals("redirect:/admin/classes", viewName);
    }

    @Test
    void testDeleteClass() {
        doNothing().when(schoolClassRepository).delete(1L);

        String viewName = adminClassesController.deleteClass(1L);

        verify(schoolClassRepository).delete(1L);
        assertEquals("redirect:/admin/classes", viewName);
    }
}
