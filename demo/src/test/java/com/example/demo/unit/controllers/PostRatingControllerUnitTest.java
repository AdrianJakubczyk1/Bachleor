package com.example.demo.unit.controllers;

import com.example.demo.PostRatingController;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.PostRating;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.PostRatingRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;

class PostRatingControllerUnitTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostRatingRepository postRatingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PostRatingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRatePost_InvalidRatingLow() {
        Long postId = 1L;
        short invalidRating = -1;
        Principal principal = () -> "user1";

        String view = controller.ratePost(postId, invalidRating, principal);

        assertEquals("redirect:/posts/1?error=invalidRating", view);
        verifyNoInteractions(userRepository, postRatingRepository, postRepository);
    }

    @Test
    void testRatePost_InvalidRatingHigh() {
        Long postId = 1L;
        short invalidRating = 11;
        Principal principal = () -> "user1";

        String view = controller.ratePost(postId, invalidRating, principal);

        assertEquals("redirect:/posts/1?error=invalidRating", view);
        verifyNoInteractions(userRepository, postRatingRepository, postRepository);
    }

    @Test
    void testRatePost_UserNotFound() {
        Long postId = 1L;
        short rating = 7;
        Principal principal = () -> "nonexistentUser";
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        String view = controller.ratePost(postId, rating, principal);

        assertEquals("redirect:/posts/1?error=userNotFound", view);
        verify(userRepository).findByUsername("nonexistentUser");
        verifyNoMoreInteractions(postRatingRepository, postRepository);
    }

    @Test
    void testRatePost_AlreadyRated() {
        Long postId = 1L;
        short rating = 8;
        Principal principal = () -> "user1";
        User user = new User();
        user.setId(100L);
        user.setUsername("user1");
        when(userRepository.findByUsername("user1")).thenReturn(user);

        // Simulate that there is already an existing rating.
        PostRating existingRating = new PostRating();
        when(postRatingRepository.findByPostIdAndUserId(postId, 100L))
                .thenReturn(Optional.of(existingRating));

        String view = controller.ratePost(postId, rating, principal);

        assertEquals("redirect:/posts/1?error=alreadyRated", view);
        verify(userRepository).findByUsername("user1");
        verify(postRatingRepository).findByPostIdAndUserId(postId, 100L);
        verifyNoMoreInteractions(postRatingRepository, postRepository);
    }

    @Test
    void testRatePost_SuccessfulRating() {
        Long postId = 1L;
        short rating = 9;
        Principal principal = () -> "user1";

        User user = new User();
        user.setId(200L);
        user.setUsername("user1");
        when(userRepository.findByUsername("user1")).thenReturn(user);

        when(postRatingRepository.findByPostIdAndUserId(postId, 200L))
                .thenReturn(Optional.empty());


        when(postRatingRepository.findByPostId(postId)).thenReturn(Arrays.asList(
                new PostRating() {{ setRating((short)7); }},
                new PostRating() {{ setRating((short)9); }},
                new PostRating() {{ setRating((short)9); }}
        ));

        Post post = new Post();
        post.setId(postId);
        post.setAvgRating(0.0);
        when(postRepository.findById(postId)).thenReturn(post);
        String view = controller.ratePost(postId, rating, principal);
        assertEquals("redirect:/posts/1", view);
        verify(userRepository).findByUsername("user1");
        verify(postRatingRepository).findByPostIdAndUserId(postId, 200L);
        ArgumentCaptor<PostRating> ratingCaptor = ArgumentCaptor.forClass(PostRating.class);
        verify(postRatingRepository).save(ratingCaptor.capture());
        PostRating savedRating = ratingCaptor.getValue();
        assertEquals(postId, savedRating.getPostId());
        assertEquals(200L, savedRating.getUserId());
        assertEquals(rating, savedRating.getRating());

        verify(postRatingRepository).findByPostId(postId);
        ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
        verify(postRepository).save(postCaptor.capture());
        Post updatedPost = postCaptor.getValue();
        double expectedAvg = (7 + 9 + 9) / 3.0;
        assertEquals(expectedAvg, updatedPost.getAvgRating(), 0.001);
    }
}
