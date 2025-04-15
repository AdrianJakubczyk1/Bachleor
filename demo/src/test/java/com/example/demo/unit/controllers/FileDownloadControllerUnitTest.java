package com.example.demo.unit.controllers;

import com.example.demo.FileDownloadController;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileDownloadControllerUnitTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private FileDownloadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test when post is not found.
    @Test
    void testDownloadAttachment_PostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = controller.downloadAttachment(postId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Test when post exists but attachment is null.
    @Test
    void testDownloadAttachment_NoAttachment() {
        Long postId = 2L;
        Post post = new Post();
        // Simulate that no attachment is available.
        post.setAttachment(null);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseEntity<byte[]> response = controller.downloadAttachment(postId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    // Test when post exists and attachment is present.
    @Test
    void testDownloadAttachment_Success() {
        Long postId = 3L;
        Post post = new Post();
        byte[] attachmentData = "dummy data".getBytes();
        String contentType = "application/pdf";
        String filename = "document.pdf";

        post.setAttachment(attachmentData);
        post.setAttachmentContentType(contentType);
        post.setAttachmentFilename(filename);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        ResponseEntity<byte[]> response = controller.downloadAttachment(postId);

        // Verify status code.
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify response content.
        assertArrayEquals(attachmentData, response.getBody());

        // Verify headers.
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.parseMediaType(contentType), headers.getContentType());

        ContentDisposition disposition = headers.getContentDisposition();
        assertNotNull(disposition);
        assertEquals("attachment", disposition.getType());
        assertEquals(filename, disposition.getFilename());
    }
}
