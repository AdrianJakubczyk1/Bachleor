package com.example.demo.integration;

import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {

                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
public class LessonControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private LessonRatingRepository lessonRatingRepository;

    private Lesson lesson;
    private User user;

    @BeforeEach
    void setup() {
        lessonRatingRepository.deleteAll();
        classSignUpRepository.deleteAll();
        lessonRepository.deleteAll();
        schoolClassRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUsername("john");
        user.setPassword("p");
        user.setRole("USER");
        user = userRepository.save(user);

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName("TestClass");
        schoolClass.setTeacherId(user.getId());
        schoolClass = schoolClassRepository.save(schoolClass);

        lesson = new Lesson();
        lesson.setSchoolClassId(schoolClass.getId());
        lesson.setTitle("Test Lesson");
        lesson.setDescription("Desc");
        lesson.setContent("Content");
        lesson = lessonRepository.save(lesson);
    }

    @Test
    void lessonNotFoundRedirectsToClasses() throws Exception {
        mockMvc.perform(get("/lessons/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
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
                        .with(user("john").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/classes/" + lesson.getSchoolClassId()));
    }

    @Test
    void approvedUserSeesLessonDetailWithoutRating() throws Exception {
        ClassSignUp signup = new ClassSignUp();
        signup.setSchoolClassId(lesson.getSchoolClassId());
        signup.setUserId(user.getId());
        signup.setStatus("APPROVED");
        signup.setCreatedDate(LocalDateTime.now());
        classSignUpRepository.save(signup);

        mockMvc.perform(get("/lessons/" + lesson.getId())
                        .with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("lessonDetail"))
                .andExpect(model().attribute("lesson", hasProperty("id", is(lesson.getId()))))
                .andExpect(model().attribute("averageRating", is(0.0)))
                .andExpect(model().attribute("userRated", is(false)));
    }

    @Test
    void approvedUserSeesLessonDetailWithRating() throws Exception {
        ClassSignUp signup = new ClassSignUp();
        signup.setSchoolClassId(lesson.getSchoolClassId());
        signup.setUserId(user.getId());
        signup.setStatus("APPROVED");
        signup.setCreatedDate(LocalDateTime.now());
        classSignUpRepository.save(signup);

        LessonRating rating = new LessonRating();
        rating.setLessonId(lesson.getId());
        rating.setUserId(user.getId());
        rating.setRating(4);
        rating.setCreatedDate(LocalDateTime.now());
        lessonRatingRepository.save(rating);

        mockMvc.perform(get("/lessons/" + lesson.getId())
                        .with(user("john").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("lessonDetail"))
                .andExpect(model().attribute("lesson", hasProperty("id", is(lesson.getId()))))
                .andExpect(model().attribute("averageRating", is(4.0)))
                .andExpect(model().attribute("userRated", is(true)));
    }

}
