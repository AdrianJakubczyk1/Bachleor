package com.example.demo.integration;

import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AdminClassStudentsControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired SchoolClassRepository schoolClassRepo;
    @Autowired UserRepository userRepo;
    @Autowired ClassSignUpRepository signupRepo;

    private SchoolClass mathClass;
    private User teacher;
    private User student;
    private ClassSignUp signup;

    @BeforeEach
    void setUp() {
        // clean up
        signupRepo.findAll().forEach(s -> signupRepo.delete(s.getId()));
        schoolClassRepo.findAll().forEach(c -> schoolClassRepo.delete(c.getId()));
        userRepo.findAll().forEach(u -> userRepo.delete(u.getId()));

        // create teacher
        teacher = new User();
        teacher.setUsername("teach");
        teacher.setPassword("pw");
        teacher.setFirstName("Teach");
        teacher.setLastName("Er");
        teacher.setRole("TEACHER");
        teacher = userRepo.save(teacher);

        // create class and assign teacher
        mathClass = new SchoolClass();
        mathClass.setName("Math 101");
        mathClass.setTeacherId(teacher.getId());
        mathClass = schoolClassRepo.save(mathClass);

        // create a student and sign them up
        student = new User();
        student.setUsername("jdoe");
        student.setPassword("pw");
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setRole("USER");
        student = userRepo.save(student);

        signup = new ClassSignUp();
        signup.setSchoolClassId(mathClass.getId());
        signup.setUserId(student.getId());
        signup.setStatus("APPROVED");
        signup.setCreatedDate(LocalDateTime.now());
        signup = signupRepo.create(signup);
    }

    @Test
    @WithMockUser(username="teach", roles="TEACHER")
    void viewClassStudents_showsStudent() throws Exception {
        mockMvc.perform(get("/teacher/classes/" + mathClass.getId() + "/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("teacherClassStudents"))
                .andExpect(model().attributeExists("schoolClass"))
                .andExpect(model().attributeExists("signupDetails"))
                .andExpect(model().attributeExists("lessons"));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void approveStudent_removesSignup() throws Exception {
        // ensure it exists first
        Optional<ClassSignUp> before = signupRepo.findById(signup.getId());
        assertThat(before).isPresent();

        mockMvc.perform(post("/teacher/classes/"
                        + mathClass.getId()
                        + "/students/"
                        + signup.getId()
                        + "/approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/classes/" + mathClass.getId() + "/students"));

        // after approve, signupRepo.findById should be empty
        Optional<ClassSignUp> after = signupRepo.findById(signup.getId());
        assertThat(after).isNotPresent();
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void rejectStudent_removesSignup() throws Exception {
        // recreate signup since previous test deleted it
        setUp();

        mockMvc.perform(post("/teacher/classes/"
                        + mathClass.getId()
                        + "/students/"
                        + signup.getId()
                        + "/reject"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher/classes/" + mathClass.getId() + "/students"));

        assertThat(signupRepo.findById(signup.getId())).isNotPresent();
    }
}