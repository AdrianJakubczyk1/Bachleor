package com.example.demo.unit.controllers;

import com.example.demo.AdminLessonController;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminLessonControllerUnitTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminLessonController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListLessons() {
        Long classId = 1L;
        Lesson lesson1 = new Lesson();
        lesson1.setTitle("Lesson 1");
        Lesson lesson2 = new Lesson();
        lesson2.setTitle("Lesson 2");
        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);

        when(lessonRepository.findBySchoolClassId(classId)).thenReturn(lessons);

        String viewName = controller.listLessons(classId, model);

        verify(model).addAttribute("lessons", lessons);
        verify(model).addAttribute("classId", classId);
        assertEquals("adminLessonList", viewName);
    }

    @Test
    void testNewLesson() {
        Long classId = 1L;

        String viewName = controller.newLesson(classId, model);

        verify(model).addAttribute("classId", classId);
        assertEquals("adminLessonForm", viewName);
    }

    @Test
    void testAddLessonWithoutFile() {
        Long classId = 1L;
        String title = "Lesson Title";
        String description = "Lesson Description";
        String content = "Lesson Content";
        // Simulate file being null (no attachment)

        String redirectUrl = controller.addLesson(classId, title, description, content, null);

        ArgumentCaptor<Lesson> lessonCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson savedLesson = lessonCaptor.getValue();

        assertEquals(classId, savedLesson.getSchoolClassId());
        assertEquals(title, savedLesson.getTitle());
        assertEquals(description, savedLesson.getDescription());
        assertEquals(content, savedLesson.getContent());
        assertNull(savedLesson.getAttachment(), "Attachment should be null when no file is provided");
        assertNull(savedLesson.getAttachmentFilename());
        assertNull(savedLesson.getAttachmentContentType());
        assertEquals("redirect:/admin/classes/" + classId + "/lessons", redirectUrl);
    }

    @Test
    void testAddLessonWithFile() throws IOException {
        Long classId = 1L;
        String title = "File Lesson Title";
        String description = "File Lesson Description";
        String content = "File Lesson Content";

        // Create a mock file to simulate an uploaded file
        byte[] fileContent = "dummy content".getBytes();
        MultipartFile file = new MockMultipartFile("file", "dummy.txt", "text/plain", fileContent);

        String redirectUrl = controller.addLesson(classId, title, description, content, file);

        ArgumentCaptor<Lesson> lessonCaptor = ArgumentCaptor.forClass(Lesson.class);
        verify(lessonRepository).save(lessonCaptor.capture());
        Lesson savedLesson = lessonCaptor.getValue();

        assertEquals(classId, savedLesson.getSchoolClassId());
        assertEquals(title, savedLesson.getTitle());
        assertEquals(description, savedLesson.getDescription());
        assertEquals(content, savedLesson.getContent());
        assertArrayEquals(fileContent, savedLesson.getAttachment());
        assertEquals("dummy.txt", savedLesson.getAttachmentFilename());
        assertEquals("text/plain", savedLesson.getAttachmentContentType());
        assertEquals("redirect:/admin/classes/" + classId + "/lessons", redirectUrl);
    }
}