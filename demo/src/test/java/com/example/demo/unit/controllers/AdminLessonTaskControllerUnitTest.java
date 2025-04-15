package com.example.demo.unit.controllers;
import com.example.demo.AdminLessonTaskController;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.repository.LessonTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLessonTaskControllerUnitTest {

    @Mock
    private LessonTaskRepository lessonTaskRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminLessonTaskController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListTasks() {
        Long lessonId = 1L;

        LessonTask task1 = new LessonTask();
        task1.setTitle("Task 1");
        LessonTask task2 = new LessonTask();
        task2.setTitle("Task 2");
        List<LessonTask> tasks = Arrays.asList(task1, task2);

        when(lessonTaskRepository.findByLessonId(lessonId)).thenReturn(tasks);

        String viewName = controller.listTasks(lessonId, model);

        verify(model).addAttribute("tasks", tasks);
        verify(model).addAttribute("lessonId", lessonId);
        assertEquals("adminLessonTaskList", viewName);
    }

    @Test
    void testNewTask() {
        Long lessonId = 2L;

        String viewName = controller.newTask(lessonId, model);

        verify(model).addAttribute("lessonId", lessonId);
        assertEquals("adminLessonTaskForm", viewName);
    }

    @Test
    void testCreateTaskWithValidDueDate() {
        Long lessonId = 3L;
        String title = "New Task";
        String description = "Task Description";
        // Create a valid due date string using ISO_LOCAL_DATE_TIME format.
        LocalDateTime now = LocalDateTime.now().plusDays(5);
        String dueDate = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // We pass true for solutionRequired.
        String result = controller.createTask(lessonId, title, description, dueDate, true);

        ArgumentCaptor<LessonTask> lessonTaskCaptor = ArgumentCaptor.forClass(LessonTask.class);
        verify(lessonTaskRepository).save(lessonTaskCaptor.capture());
        LessonTask savedTask = lessonTaskCaptor.getValue();

        assertEquals(lessonId, savedTask.getLessonId());
        assertEquals(title, savedTask.getTitle());
        assertEquals(description, savedTask.getDescription());
        // Check that the due date was parsed correctly.
        assertEquals(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                savedTask.getDueDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // Use getSolutionRequired() instead of isSolutionRequired().
        assertTrue(savedTask.getSolutionRequired());

        assertEquals("redirect:/admin/lessons/" + lessonId + "/tasks", result);
    }

    @Test
    void testCreateTaskWithInvalidDueDate() {
        Long lessonId = 4L;
        String title = "Task with bad date";
        String description = "This task uses an invalid due date format";
        String invalidDueDate = "invalid-date-format";

        // We pass false for solutionRequired.
        String result = controller.createTask(lessonId, title, description, invalidDueDate, false);

        ArgumentCaptor<LessonTask> lessonTaskCaptor = ArgumentCaptor.forClass(LessonTask.class);
        verify(lessonTaskRepository).save(lessonTaskCaptor.capture());
        LessonTask savedTask = lessonTaskCaptor.getValue();

        assertEquals(lessonId, savedTask.getLessonId());
        assertEquals(title, savedTask.getTitle());
        assertEquals(description, savedTask.getDescription());
        // Since parsing fails, the default due date is set to 7 days later than now.
        LocalDateTime expectedDueDate = LocalDateTime.now().plusDays(7);
        // Allowing a margin for execution time.
        long secondsDifference = java.time.Duration.between(expectedDueDate, savedTask.getDueDate()).getSeconds();
        assertTrue(Math.abs(secondsDifference) < 5, "The default due date should be roughly 7 days later");
        // Use getSolutionRequired() for the flag.
        assertFalse(savedTask.getSolutionRequired());

        assertEquals("redirect:/admin/lessons/" + lessonId + "/tasks", result);
    }
}