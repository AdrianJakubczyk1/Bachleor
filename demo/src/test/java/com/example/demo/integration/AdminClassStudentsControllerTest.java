package com.example.demo.integration;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminClassStudentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM class_sign_up");
        jdbcTemplate.execute("DELETE FROM school_class");
        jdbcTemplate.execute("DELETE FROM user");

        // Seed test data
        jdbcTemplate.update("INSERT INTO school_class(id, name) VALUES (?,?)", 1, "Math 101");

        jdbcTemplate.update("INSERT INTO user(id, first_name, last_name, role) VALUES (?,?,?,?)",
                100, "John", "Doe", "USER");
        jdbcTemplate.update("INSERT INTO user(id, first_name, last_name, role) VALUES (?,?,?,?)",
                101, "Jane", "Smith", "USER");
        jdbcTemplate.update("INSERT INTO user(id, first_name, last_name, role) VALUES (?,?,?,?)",
                102, "Bob", "Admin", "ADMIN");

        jdbcTemplate.update("INSERT INTO class_sign_up(id, school_class_id, user_id, status, created_date) VALUES (?,?,?,?,?)",
                1, 1, 100, "APPROVED", LocalDateTime.now());
    }

    @Test
    void testViewClassStudents_Success() throws Exception {
        mockMvc.perform(get("/admin/classes/1/students"))
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
        mockMvc.perform(post("/admin/classes/1/students/1/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/1/students"));

        // Ensure student enrollment was deleted
        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM class_sign_up WHERE id = ?", Integer.class, 1);
        assert(count == 0);
    }

    @Test
    void testAddStudent_AlreadyExists() throws Exception {
        mockMvc.perform(post("/admin/classes/1/students/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/1/students"));

        // Ensure no new duplicate entry was created
        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM class_sign_up WHERE school_class_id=? AND user_id=?",
                Integer.class, 1, 100);
        assert(count == 1);
    }

    @Test
    void testAddStudent_NewEnrollment() throws Exception {
        mockMvc.perform(post("/admin/classes/1/students/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("studentId", "101"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/classes/1/students"));

        // Ensure a new enrollment is created
        int count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM class_sign_up WHERE school_class_id=? AND user_id=?",
                Integer.class, 1, 101);
        assert(count == 1);
    }
}
