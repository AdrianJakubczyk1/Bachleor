package com.example.demo.unit.controllers;

import com.example.demo.TeacherTaskSubmissionController;
import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TeacherTaskSubmissionControllerUnitTest {

    @Mock
    private TaskSubmissionRepository taskSubmissionRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherTaskSubmissionController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testListUngradedSubmissions() {
        TaskSubmission sub1 = new TaskSubmission();
        TaskSubmission sub2 = new TaskSubmission();
        when(taskSubmissionRepository.findByGradeIsNull()).thenReturn(Arrays.asList(sub1, sub2));

        // Act
        String view = controller.listUngradedSubmissions(model);

        // Assert
        verify(model).addAttribute("submissions", Arrays.asList(sub1, sub2));
        assertEquals("teacherSubmissions", view);
    }
    @Test
    void testDownloadSubmissionFile_Success() {
        Long submissionId = 100L;
        TaskSubmission submission = new TaskSubmission();
        byte[] fileBytes = "Test file content".getBytes(StandardCharsets.UTF_8);
        submission.setSolutionFile(fileBytes);
        submission.setSolutionContentType("application/pdf");
        submission.setSolutionFilename("solution.pdf");

        when(taskSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        ResponseEntity<byte[]> response = controller.downloadSubmissionFile(submissionId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileBytes, response.getBody());
        HttpHeaders headers = response.getHeaders();
        MediaType expectedMediaType = MediaType.parseMediaType("application/pdf");
        assertEquals(expectedMediaType, headers.getContentType());
        ContentDisposition contentDisposition = headers.getContentDisposition();
        assertNotNull(contentDisposition);
        assertEquals("attachment", contentDisposition.getType());
        assertEquals("solution.pdf", contentDisposition.getFilename());
    }
    @Test
    void testDownloadSubmissionFile_SubmissionNotFound() {
        Long submissionId = 200L;
        when(taskSubmissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = controller.downloadSubmissionFile(submissionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    void testDownloadSubmissionFile_NoSolutionFile() {
        Long submissionId = 300L;
        TaskSubmission submission = new TaskSubmission();
        submission.setSolutionFile(null); // No file present.
        when(taskSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        ResponseEntity<byte[]> response = controller.downloadSubmissionFile(submissionId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGradeSubmission_InvalidGradeLow() {
        Long submissionId = 10L;
        double invalidGrade = -1.0;

        String view = controller.gradeSubmission(submissionId, invalidGrade, "Good work");

        assertEquals("redirect:/teacher/task-submissions?error=invalidGrade", view);
        verifyNoInteractions(taskSubmissionRepository);
    }
    @Test
    void testGradeSubmission_InvalidGradeHigh() {
        Long submissionId = 10L;
        double invalidGrade = 11.0;

        String view = controller.gradeSubmission(submissionId, invalidGrade, "Excellent");

        assertEquals("redirect:/teacher/task-submissions?error=invalidGrade", view);
        verifyNoInteractions(taskSubmissionRepository);
    }
    @Test
    void testGradeSubmission_Success() {
        Long submissionId = 20L;
        double validGrade = 8.5;
        String teacherComments = "Well done";
        TaskSubmission submission = new TaskSubmission();
        when(taskSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        String view = controller.gradeSubmission(submissionId, validGrade, teacherComments);
        assertEquals(validGrade, submission.getGrade());
        assertEquals(teacherComments, submission.getTeacherComments());
        verify(taskSubmissionRepository).save(submission);
        assertEquals("redirect:/teacher/task-submissions", view);
    }
}
