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
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class AdminClassStudentsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private SchoolClassRepository schoolClassRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ClassSignUpRepository classSignUpRepository;

    private SchoolClass mathClass;
    private User teacher;
    private User user1;
    private User user2;
    private User admin;
    private ClassSignUp signup;

    @BeforeEach
    void setup() {
        classSignUpRepository.deleteAll();
        schoolClassRepository.deleteAll();
        userRepository.deleteAll();

        teacher = new User();
        teacher.setUsername("teach");
        teacher.setPassword("pw");
        teacher.setFirstName("Teach");
        teacher.setLastName("Er");
        teacher.setRole("TEACHER");
        teacher = userRepository.save(teacher);

        mathClass = new SchoolClass();
        mathClass.setName("Math 101");
        mathClass.setTeacherId(teacher.getId());
        mathClass = schoolClassRepository.save(mathClass);

        user1 = new User();
        user1.setUsername("john");
        user1.setPassword("password123");
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setRole("USER");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setUsername("jane");
        user2.setPassword("password123");
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setRole("USER");
        user2 = userRepository.save(user2);

        admin = new User();
        admin.setUsername("bob");
        admin.setPassword("password123");
        admin.setFirstName("Bob");
        admin.setLastName("Admin");
        admin.setRole("ADMIN");
        admin = userRepository.save(admin);

        signup = new ClassSignUp();
        signup.setSchoolClassId(mathClass.getId());
        signup.setUserId(user1.getId());
        signup.setStatus("APPROVED");
        signup.setCreatedDate(LocalDateTime.now());
        signup = classSignUpRepository.save(signup);
    }

    @Test
    void testViewClassStudents_Success() throws Exception {
        mockMvc.perform(get("/admin/classes/" + mathClass.getId() + "/students").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("adminClassStudents"))
                .andExpect(model().attributeExists("schoolClass"))
                .andExpect(model().attribute("signUpDetails", hasSize(1)))
                .andExpect(model().attribute("availableStudents", hasSize(1)));
    }

    @Test
    void testViewClassStudents_ClassNotFound() throws Exception {
        mockMvc.perform(get("/admin/classes/999/students"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes?error=classNotFound"));
    }

    @Test
    void testRemoveStudent() throws Exception {
        mockMvc.perform(post("/admin/classes/" + mathClass.getId()
                        + "/students/" + signup.getId() + "/remove")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/" + mathClass.getId() + "/students"));

        assertThat(classSignUpRepository.existsById(signup.getId())).isFalse();
    }

    @Test
    void testAddStudent_AlreadyExists() throws Exception {
        mockMvc.perform(post("/admin/classes/" + mathClass.getId() + "/students/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", String.valueOf(user1.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/" + mathClass.getId() + "/students"));

        long count = classSignUpRepository.countBySchoolClassIdAndUserId(mathClass.getId(), user1.getId());
        assertThat(count).isEqualTo(1);
    }

    @Test
    void testAddStudent_NewEnrollment() throws Exception {
        mockMvc.perform(post("/admin/classes/" + mathClass.getId() + "/students/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", String.valueOf(user2.getId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/" + mathClass.getId() + "/students"));

        long count = classSignUpRepository.countBySchoolClassIdAndUserId(mathClass.getId(), user2.getId());
        assertThat(count).isEqualTo(1);
    }
}