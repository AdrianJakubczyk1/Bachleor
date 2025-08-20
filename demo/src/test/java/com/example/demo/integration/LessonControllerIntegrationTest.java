package com.example.demo.integration;

import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LessonControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired LessonRepository lessonRepo;
    @Autowired UserRepository userRepo;
    @Autowired SchoolClassRepository classRepo;
    @Autowired ClassSignUpRepository signupRepo;
    @Autowired LessonRatingRepository ratingRepo;

    private Lesson lesson;
    private User user;
    private SchoolClass schoolClass;

    @BeforeEach
    void setUp() {
        // clear everything
        ratingRepo.findAll().forEach(r -> ratingRepo.delete(r.getId()));
        signupRepo.findAll().forEach(s -> signupRepo.delete(s.getId()));
        lessonRepo.findAll().forEach(l -> lessonRepo.delete(l.getId()));
        classRepo.findAll().forEach(c -> classRepo.delete(c.getId()));
        userRepo.findAll().forEach(u -> userRepo.delete(u.getId()));

        // create a user
        user = new User();
        user.setUsername("john");
        user.setPassword("p");
        user.setRole("USER");
        user = userRepo.save(user);

        // create a class
        schoolClass = new SchoolClass();
        schoolClass.setName("TestClass");
        schoolClass.setTeacherId(user.getId());
        schoolClass = classRepo.save(schoolClass);

        // create a lesson
        lesson = new Lesson();
        lesson.setSchoolClassId(schoolClass.getId());
        lesson.setTitle("Test Lesson");
        lesson.setDescription("Desc");
        lesson.setContent("Content");
        lesson = lessonRepo.save(lesson);
    }

    @Test
    void lessonNotFoundRedirectsToClasses() throws Exception {
        mockMvc.perform(get("/lessons/999"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void notLoggedInRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/lessons/" + lesson.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    void userNotSignedUpRedirectsToClassPage() throws Exception {
        mockMvc.perform(get("/lessons/" + lesson.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("john").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes/" + lesson.getSchoolClassId()));
    }

    @Test
    void approvedUserSeesLessonDetailWithoutRating() throws Exception {
        // sign up the user
        ClassSignUp su = new ClassSignUp();
        su.setSchoolClassId(lesson.getSchoolClassId());
        su.setUserId(user.getId());
        su.setStatus("APPROVED");
        su.setCreatedDate(LocalDateTime.now());
        signupRepo.save(su);

        mockMvc.perform(get("/lessons/" + lesson.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("lessonDetail"))
                .andExpect(model().attribute("lesson",
                        hasProperty("id", is(lesson.getId()))))
                .andExpect(model().attribute("averageRating", is(0.0)))
                .andExpect(model().attribute("userRated", is(false)));
    }

    @Test
    void approvedUserSeesLessonDetailWithRating() throws Exception {
        // sign up
        ClassSignUp su = new ClassSignUp();
        su.setSchoolClassId(lesson.getSchoolClassId());
        su.setUserId(user.getId());
        su.setStatus("APPROVED");
        su.setCreatedDate(LocalDateTime.now());
        signupRepo.save(su);

        // rate
        LessonRating rating = new LessonRating();
        rating.setLessonId(lesson.getId());
        rating.setUserId(user.getId());
        rating.setRating(4);
        rating.setCreatedDate(LocalDateTime.now());
        ratingRepo.save(rating);

        mockMvc.perform(get("/lessons/" + lesson.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("lessonDetail"))
                .andExpect(model().attribute("lesson",
                        hasProperty("id", is(lesson.getId()))))
                .andExpect(model().attribute("averageRating", is(4.0)))
                .andExpect(model().attribute("userRated", is(true)));
    }
}
