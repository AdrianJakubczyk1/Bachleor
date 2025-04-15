package com.example.demo.unit.controllers;

import com.example.demo.LessonController;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonRating;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.LessonRatingRepository;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LessonControllerUnitTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonRatingRepository lessonRatingRepository;

    @Mock
    private Model model;

    @InjectMocks
    private LessonController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testLessonDetail_LessonNotFound() {
        Long lessonId = 100L;
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.empty());

        String view = controller.lessonDetail(lessonId, model, () -> "user1");

        assertEquals("redirect:/classes", view);
    }

    @Test
    void testLessonDetail_NotLoggedIn() {
        Long lessonId = 101L;
        Lesson lesson = new Lesson();
        lesson.setSchoolClassId(10L);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        String view = controller.lessonDetail(lessonId, model, null);

        assertEquals("redirect:/login", view);
        verify(model).addAttribute("loggedIn", false);
    }

    @Test
    void testLessonDetail_UserNotFound() {
        Long lessonId = 102L;
        Lesson lesson = new Lesson();
        lesson.setSchoolClassId(20L);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        // Simulate a principal with a username.
        Principal principal = () -> "nonexistentUser";
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        String view = controller.lessonDetail(lessonId, model, principal);

        assertEquals("redirect:/login", view);
    }

    @Test
    void testLessonDetail_NotAllowedForNonAdmin() {
        Long lessonId = 103L;
        Lesson lesson = new Lesson();
        lesson.setSchoolClassId(30L);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        Principal principal = () -> "userX";
        User user = new User();
        user.setId(200L);
        user.setUsername("userX");
        when(userRepository.findByUsername("userX")).thenReturn(user);
        when(classSignUpRepository.findBySchoolClassIdAndUserId(30L, 200L)).thenReturn(Optional.empty());
        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.lessonDetail(lessonId, model, principal);
        assertEquals("redirect:/classes/30", view);
    }

    @Test
    void testLessonDetail_AllowedForNonAdmin() {
        Long lessonId = 104L;
        Lesson lesson = new Lesson();
        lesson.setSchoolClassId(40L);
        when(lessonRepository.findById(lessonId)).thenReturn(Optional.of(lesson));

        Principal principal = () -> "userY";
        User user = new User();
        user.setId(300L);
        user.setUsername("userY");
        when(userRepository.findByUsername("userY")).thenReturn(user);

        // Simulate approved sign-up.
        ClassSignUp signup = new ClassSignUp();
        signup.setSchoolClassId(40L);
        signup.setUserId(300L);
        signup.setStatus("APPROVED");
        when(classSignUpRepository.findBySchoolClassIdAndUserId(40L, 300L))
                .thenReturn(Optional.of(signup));

        LessonRating rating1 = new LessonRating();
        rating1.setRating(4);
        LessonRating rating2 = new LessonRating();
        rating2.setRating(5);
        when(lessonRatingRepository.findByLessonId(lessonId))
                .thenReturn(Arrays.asList(rating1, rating2));

        when(lessonRatingRepository.findByLessonIdAndUserId(lessonId, 300L))
                .thenReturn(Optional.empty());

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        String view = controller.lessonDetail(lessonId, model, principal);

        verify(model).addAttribute("loggedIn", true);

        verify(model).addAttribute("isAdmin", false);

        verify(model).addAttribute("lesson", lesson);

        verify(model).addAttribute("averageRating", 4.5);

        verify(model).addAttribute("userRated", false);

        assertEquals("lessonDetail", view);
    }
}
