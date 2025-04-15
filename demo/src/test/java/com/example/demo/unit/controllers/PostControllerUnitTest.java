package com.example.demo.unit.controllers;

import com.example.demo.PostController;
import com.example.demo.persistent.model.Comment;
import com.example.demo.persistent.model.CommentLike;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.PostRating;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.CommentLikeRepository;
import com.example.demo.persistent.repository.CommentRepository;
import com.example.demo.persistent.repository.PostRatingRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.springframework.mock.web.MockHttpServletRequest;

class PostControllerUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @Mock
    private PostRatingRepository postRatingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private PostController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPostDetail_PostExists_LoggedIn_AlreadyRated() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        post.setViewCount(10);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        Principal principal = () -> "testUser";
        User user = new User();
        user.setId(100L);
        user.setUsername("testUser");
        when(userRepository.findByUsername("testUser")).thenReturn(user);
        PostRating existingRating = new PostRating();
        when(postRatingRepository.findByPostIdAndUserId(postId, 100L))
                .thenReturn(Optional.of(existingRating));

        Comment comment1 = new Comment();
        comment1.setId(10L);
        comment1.setPostId(postId);
        Comment comment2 = new Comment();
        comment2.setId(20L);
        comment2.setPostId(postId);
        List<Comment> comments = Arrays.asList(comment1, comment2);
        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        when(commentLikeRepository.countByCommentId(10L)).thenReturn(5);
        when(commentLikeRepository.countByCommentId(20L)).thenReturn(3);
        // Simulate that testUser liked comment1, but not comment2.
        when(commentLikeRepository.findByCommentIdAndUsername(10L, "testUser"))
                .thenReturn(Optional.of(new CommentLike()));
        when(commentLikeRepository.findByCommentIdAndUsername(20L, "testUser"))
                .thenReturn(Optional.empty());

        String view = controller.postDetail(postId, model, principal);

        assertEquals(11, post.getViewCount());
        verify(postRepository).save(post);
        verify(model).addAttribute("post", post);
        verify(model).addAttribute("comments", comments);
        verify(model).addAttribute("alreadyRated", true);
        verify(model).addAttribute("loggedIn", true);
        verify(model).addAttribute("username", "testUser");
        ArgumentCaptor<Map> countsCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> likedCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("commentLikeCounts"), countsCaptor.capture());
        verify(model).addAttribute(eq("commentUserLiked"), likedCaptor.capture());

        Map<Long, Integer> likeCounts = countsCaptor.getValue();
        Map<Long, Boolean> userLiked = likedCaptor.getValue();
        assertEquals(5, likeCounts.get(10L));
        assertEquals(3, likeCounts.get(20L));
        assertTrue(userLiked.get(10L));
        assertFalse(userLiked.get(20L));

        assertEquals("postDetail", view);
    }
    @Test
    void testPostDetail_PostExists_Anonymous() {
        Long postId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setViewCount(20);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Principal principal = null;
        Comment comment = new Comment();
        comment.setId(30L);
        comment.setPostId(postId);
        List<Comment> comments = Collections.singletonList(comment);
        when(commentRepository.findByPostId(postId)).thenReturn(comments);

        when(commentLikeRepository.countByCommentId(30L)).thenReturn(7);
        when(commentLikeRepository.findByCommentIdAndUsername(anyLong(), anyString()))
                .thenReturn(Optional.empty());
        String view = controller.postDetail(postId, model, principal);

        assertEquals(21, post.getViewCount());
        verify(postRepository).save(post);

        verify(model).addAttribute("post", post);
        verify(model).addAttribute("comments", comments);
        verify(model).addAttribute("loggedIn", false);
        ArgumentCaptor<Map> countsCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map> likedCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("commentLikeCounts"), countsCaptor.capture());
        verify(model).addAttribute(eq("commentUserLiked"), likedCaptor.capture());
        Map<Long, Integer> likeCounts = countsCaptor.getValue();
        Map<Long, Boolean> userLiked = likedCaptor.getValue();
        assertEquals(7, likeCounts.get(30L));
        assertFalse(userLiked.get(30L));

        assertEquals("postDetail", view);
    }

    @Test
    void testPostDetail_PostNotFound() {
        Long postId = 999L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        String view = controller.postDetail(postId, model, () -> "anyUser");
        assertEquals("redirect:/", view);
    }

    @Test
    void testAddComment_PrincipalNull() {
        Long postId = 1L;
        String text = "Test comment";
        String view = controller.addComment(postId, text, null);
        assertEquals("redirect:/login", view);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void testAddComment_Success() {
        Long postId = 1L;
        String text = "Great post!";
        Principal principal = () -> "commenter";
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        String view = controller.addComment(postId, text, principal);
        verify(commentRepository).save(captor.capture());
        Comment savedComment = captor.getValue();
        assertEquals(postId, savedComment.getPostId());
        assertEquals("commenter", savedComment.getUsername());
        assertEquals(text, savedComment.getText());
        assertNotNull(savedComment.getCreatedDate());
        assertEquals(0, savedComment.getLikes());
        assertEquals("redirect:/posts/" + postId, view);
    }
    @Test
    void testLikeComment_PrincipalNull() {
        Long commentId = 10L;
        String view = controller.likeComment(commentId, null);
        assertEquals("redirect:/login", view);
        verifyNoInteractions(commentLikeRepository);
    }

    @Test
    void testLikeComment_Success_NewLike() {
        Long commentId = 20L;
        Principal principal = () -> "likerUser";
        when(commentLikeRepository.findByCommentIdAndUsername(commentId, "likerUser"))
                .thenReturn(Optional.empty());
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPostId(5L);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        String view = controller.likeComment(commentId, principal);
        ArgumentCaptor<CommentLike> captor = ArgumentCaptor.forClass(CommentLike.class);
        verify(commentLikeRepository).save(captor.capture());
        CommentLike savedLike = captor.getValue();
        assertEquals(commentId, savedLike.getCommentId());
        assertEquals("likerUser", savedLike.getUsername());
        assertEquals("redirect:/posts/5", view);
    }

    @Test
    void testLikeComment_CommentNotFound() {
        Long commentId = 30L;
        Principal principal = () -> "user";
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        String view = controller.likeComment(commentId, principal);
        assertEquals("redirect:/", view);
    }
}