package com.example.demo.unit.controllers;

import com.example.demo.SolutionSubmissionController;
import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SolutionSubmissionControllerUnitTest {

    @Mock
    private TaskSubmissionRepository taskSubmissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private SolutionSubmissionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testShowSolutionForm() {
        Long classId = 10L;
        Long lessonId = 20L;
        Long taskId = 30L;

        String view = controller.showSolutionForm(classId, lessonId, taskId, model);

        ArgumentCaptor<TaskSubmission> submissionCaptor = ArgumentCaptor.forClass(TaskSubmission.class);
        verify(model).addAttribute(eq("submission"), submissionCaptor.capture());
        TaskSubmission submission = submissionCaptor.getValue();

        assertEquals(lessonId, submission.getLessonTaskId(), "Submission lessonTaskId should be set to lessonId");
        verify(model).addAttribute("classId", classId);
        verify(model).addAttribute("lessonId", lessonId);
        verify(model).addAttribute("taskId", taskId);

        assertEquals("solutionSubmissionForm", view);
    }


}
