package com.example.demo.unit.controllers;

import com.example.demo.TeacherPostController;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.PostRepository;
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

class TeacherPostControllerUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherPostController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListTeacherPosts() {
        // Arrange
        String teacherName = "teacherName";
        Principal principal = () -> teacherName;

        User teacher = new User();
        teacher.setId(101L);
        teacher.setUsername(teacherName);
        when(userRepository.findByUsername(teacherName)).thenReturn(teacher);

        Post post1 = new Post();
        post1.setId(1L);
        post1.setAuthor(teacherName);
        Post post2 = new Post();
        post2.setId(2L);
        post2.setAuthor(teacherName);
        List<Post> posts = Arrays.asList(post1, post2);

        when(postRepository.findByAuthor(teacherName)).thenReturn(posts);

        String view = controller.listTeacherPosts(model, principal);

        verify(model).addAttribute("posts", posts);
        assertEquals("teacherPosts", view);
    }

    @Test
    void testShowAddPostForm() {
        // Act
        String view = controller.showAddPostForm(model);

        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(model).addAttribute(eq("post"), postCaptor.capture());
        assertNotNull(postCaptor.getValue());
        assertEquals("teacherPostForm", view);
    }

    @Test
    void testAddPost() {
        String teacherName = "teacherName";
        Principal principal = () -> teacherName;

        Post post = new Post();
        post.setTitle("New Post");
        String view = controller.addPost(post, principal);
        assertEquals(teacherName, post.getAuthor());
        verify(postRepository).save(post);
        assertEquals("redirect:/teacher/posts", view);
    }
    @Test
    void testShowEditPostForm_Valid() {
        // Arrange
        Long postId = 10L;
        String teacherName = "teacherName";
        Principal principal = () -> teacherName;

        Post post = new Post();
        post.setId(postId);
        post.setAuthor(teacherName);

        when(postRepository.findById(postId)).thenReturn(post);

        String view = controller.showEditPostForm(postId, model, principal);
        verify(model).addAttribute("post", post);
        assertEquals("teacherPostForm", view);
    }

    @Test
    void testShowEditPostForm_PostNotFound() {
        Long postId = 20L;
        Principal principal = () -> "teacherName";
        when(postRepository.findById(postId)).thenReturn(null);

        String view = controller.showEditPostForm(postId, model, principal);

        assertEquals("redirect:/teacher/posts", view);
    }

    @Test
    void testShowEditPostForm_WrongAuthor() {
        // Arrange
        Long postId = 30L;
        Principal principal = () -> "teacherName";
        Post post = new Post();
        post.setId(postId);
        post.setAuthor("otherTeacher");
        when(postRepository.findById(postId)).thenReturn(post);

        // Act
        String view = controller.showEditPostForm(postId, model, principal);
        assertEquals("redirect:/teacher/posts", view);
    }

    // Test POST /teacher/posts/{id}/edit: editPost
    @Test
    void testEditPost() {
        // Arrange
        Long postId = 40L;
        String teacherName = "teacherName";
        Principal principal = () -> teacherName;

        Post post = new Post();
        post.setTitle("Edited Title");
        // Simulate that the @ModelAttribute post doesn't have its id set.

        // Act
        String view = controller.editPost(postId, post, principal);

        // Assert: id and author should be set.
        assertEquals(postId, post.getId());
        assertEquals(teacherName, post.getAuthor());
        verify(postRepository).save(post);
        assertEquals("redirect:/teacher/posts", view);
    }
}
