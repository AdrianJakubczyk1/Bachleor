package com.example.demo.integration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import com.example.demo.persistent.repository.UserRepository;
import com.example.demo.persistent.model.User;
import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.example.demo.persistent.repository.UserRepository;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {

                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUp() {
        userRepository.findAll().forEach(r->userRepository.delete(r.getId()));
    }

    @Test
    void getRegisterPage_returns200AndContainsFormAttribute() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registrationForm"));
    }

    @Test
    void postRegister_duplicateUsername_showsFormWithUsernameError() throws Exception {
        User existing = new User();
        existing.setUsername("dup");
        existing.setPassword(passwordEncoder.encode("x"));
        existing.setEmail("dup@example.com");
        existing.setRole("USER");
        userRepository.save(existing);

        mockMvc.perform(MockMvcRequestBuilders.post("/perform_register")
                        .param("username",  "dup")
                        .param("password",  "secret")
                        .param("email",     "new@example.com")
                        .param("firstName", "New")
                        .param("lastName",  "User")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors("registrationForm", "username"));

        long total = userRepository.count();
        assertThat(total).isEqualTo(1);
    }

    @Test
    void postRegister_duplicateEmail_showsFormWithEmailError() throws Exception {
        // Setup existing user
        User existing = new User();
        existing.setUsername("u1");
        existing.setPassword(passwordEncoder.encode("x"));
        existing.setEmail("dup@example.com");
        existing.setRole("USER");
        userRepository.save(existing);

        mockMvc.perform(MockMvcRequestBuilders.post("/perform_register")
                        .param("username",  "u2")
                        .param("password",  "secret")
                        .param("email",     "dup@example.com")
                        .param("firstName", "New")
                        .param("lastName",  "User")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("register"))
                .andExpect(MockMvcResultMatchers.model()
                        .attributeHasFieldErrors("registrationForm", "email"));

        long total = userRepository.count();
        assertThat(total).isEqualTo(1);
    }
    @Test
    void getLoginPage_withErrorParam_setsLoginErrorFlag() throws Exception {
        mockMvc.perform(get("/login")
                        .param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("loginError", true))
                .andExpect(model().attribute("showLoginModal", true));
    }
}