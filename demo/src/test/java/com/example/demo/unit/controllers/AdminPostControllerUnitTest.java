package com.example.demo.unit.controllers;
import com.example.demo.AdminPostController;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.repository.PostRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminPostControllerUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AdminPostController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListPosts() {
        Post post1 = new Post();
        post1.setTitle("Post 1");
        Post post2 = new Post();
        post2.setTitle("Post 2");
        when(postRepository.findAll()).thenReturn(Arrays.asList(post1, post2));

        String viewName = controller.listPosts(model);

        verify(model).addAttribute("posts", Arrays.asList(post1, post2));
        assertEquals("adminPosts", viewName);
    }

    @Test
    void testShowAddPostForm() {
        String viewName = controller.showAddPostForm();

        // Assert
        assertEquals("adminPostForm", viewName);
    }

//    @Test
//    void testAddPostWithoutFile() throws IOException {
//        // Arrange
//        String title = "Sample Post";
//        String content = "Post Content";
//        MultipartFile file = null;
//
//        String redirect = controller.addPost(title, content, file);
//        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
//        verify(postRepository).save(captor.capture());
//        Post savedPost = captor.getValue();
//
//        assertEquals(title, savedPost.getTitle());
//        assertEquals(content, savedPost.getContent());
//        assertNull(savedPost.getAttachment(), "Attachment should be null when no file is provided.");
//        assertNull(savedPost.getAttachmentFilename());
//        assertNull(savedPost.getAttachmentContentType());
//        assertEquals("redirect:/admin/posts", redirect);
//    }

//    @Test
//    void testAddPostWithFile() throws IOException {
//        String title = "Sample Post with File";
//        String content = "Post Content with File";
//        byte[] fileBytes = "dummy data".getBytes();
//        MultipartFile file = new MockMultipartFile("file", "sample.txt", "text/plain", fileBytes);
//        String redirect = controller.addPost(title, content, file);
//        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
//        verify(postRepository).save(captor.capture());
//        Post savedPost = captor.getValue();
//
//        assertEquals(title, savedPost.getTitle());
//        assertEquals(content, savedPost.getContent());
//        assertArrayEquals(fileBytes, savedPost.getAttachment());
//        assertEquals("sample.txt", savedPost.getAttachmentFilename());
//        assertEquals("text/plain", savedPost.getAttachmentContentType());
//        assertEquals("redirect:/admin/posts", redirect);
//    }

    @Test
    void testEditPostFound() {
        Long postId = 1L;
        Post post = new Post();
        post.setTitle("Existing Post");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        String viewName = controller.editPost(postId, model);

        verify(model).addAttribute("post", post);
        assertEquals("adminPostEditForm", viewName);
    }

    @Test
    void testEditPostNotFound() {
        Long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        String viewName = controller.editPost(postId, model);
        assertEquals("redirect:/admin/posts", viewName);
    }
//
//    @Test
//    void testUpdatePostWithFile() throws IOException {
//        // Arrange
//        Long postId = 1L;
//        Post post = new Post();
//        post.setTitle("Old Title");
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        String newTitle = "New Title";
//        String newContent = "Updated Content";
//        byte[] newBytes = "new file content".getBytes();
//        MultipartFile file = new MockMultipartFile("file", "newFile.txt", "text/plain", newBytes);
//
//        String redirect = controller.updatePost(postId, newTitle, newContent, file);
//
//        assertEquals(newTitle, post.getTitle());
//        assertEquals(newContent, post.getContent());
//        assertArrayEquals(newBytes, post.getAttachment());
//        assertEquals("newFile.txt", post.getAttachmentFilename());
//        assertEquals("text/plain", post.getAttachmentContentType());
//        verify(postRepository).save(post);
//        assertEquals("redirect:/admin/posts", redirect);
//    }
//
//    @Test
//    void testUpdatePostWithoutFile() {
//        Long postId = 1L;
//        Post post = new Post();
//        post.setTitle("Old Title");
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        String newTitle = "New Title";
//        String newContent = "Updated Content";
//        MultipartFile file = null;
//
//        String redirect = controller.updatePost(postId, newTitle, newContent, file);
//
//        assertEquals(newTitle, post.getTitle());
//        assertEquals(newContent, post.getContent());
//        verify(postRepository).save(post);
//        assertEquals("redirect:/admin/posts", redirect);
//    }

    @Test
    void testDeletePost() {
        Long postId = 1L;
        String redirect = controller.deletePost(postId);

        verify(postRepository).deleteById(postId);
        assertEquals("redirect:/admin/posts", redirect);
    }
}
