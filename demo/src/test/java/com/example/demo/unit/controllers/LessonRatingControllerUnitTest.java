package com.example.demo.unit.controllers;

import com.example.demo.LessonRatingController;
import com.example.demo.persistent.model.LessonRating;
import com.example.demo.persistent.repository.LessonRatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LessonRatingControllerUnitTest {

    @Mock
    private LessonRatingRepository lessonRatingRepository;

    @InjectMocks
    private LessonRatingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testRateLesson_PrincipalNull() {
        Long lessonId = 10L;
        int rating = 4;

        String view = controller.rateLesson(lessonId, rating, null);

        assertEquals("redirect:/login", view);
        verifyNoInteractions(lessonRatingRepository);
    }
    @Test
    void testRateLesson_NewRating_NonAdmin() {
        Long lessonId = 20L;
        int rating = 5;
        Principal principal = () -> "user";

        when(lessonRatingRepository.findByLessonIdAndUserId(lessonId, 2L))
                .thenReturn(Optional.empty());

        String view = controller.rateLesson(lessonId, rating, principal);

        assertEquals("redirect:/lessons/" + lessonId, view);
        ArgumentCaptor<LessonRating> captor = ArgumentCaptor.forClass(LessonRating.class);
        verify(lessonRatingRepository).save(captor.capture());
        LessonRating savedRating = captor.getValue();

        assertEquals(lessonId, savedRating.getLessonId());
        assertEquals(2L, savedRating.getUserId());
        assertEquals(rating, savedRating.getRating());
        assertNotNull(savedRating.getCreatedDate());
    }
    @Test
    void testRateLesson_UpdateExisting_Rating_Admin() {
        Long lessonId = 30L;
        int newRatingValue = 3;
        Principal principal = () -> "admin";

        LessonRating existingRating = new LessonRating();
        existingRating.setLessonId(lessonId);
        existingRating.setUserId(1L);
        existingRating.setRating(1);
        existingRating.setCreatedDate(LocalDateTime.now().minusDays(1));

        when(lessonRatingRepository.findByLessonIdAndUserId(lessonId, 1L))
                .thenReturn(Optional.of(existingRating));

        String view = controller.rateLesson(lessonId, newRatingValue, principal);

        assertEquals("redirect:/lessons/" + lessonId, view);
        ArgumentCaptor<LessonRating> captor = ArgumentCaptor.forClass(LessonRating.class);
        verify(lessonRatingRepository).save(captor.capture());
        LessonRating savedRating = captor.getValue();
        assertEquals(lessonId, savedRating.getLessonId());
        assertEquals(1L, savedRating.getUserId());
        assertEquals(newRatingValue, savedRating.getRating());
        assertNotNull(savedRating.getCreatedDate());
    }
}
