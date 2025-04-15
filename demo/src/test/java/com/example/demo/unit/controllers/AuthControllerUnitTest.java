package com.example.demo.unit.controllers;

import com.example.demo.AuthController;
import com.example.demo.RegistrationForm;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @InjectMocks
    private AuthController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testLoginWithError() {
        String viewName = controller.login("someError", model);

        verify(model).addAttribute("loginError", true);
        verify(model).addAttribute("showLoginModal", true);
        assertEquals("login", viewName);
    }

    @Test
    void testLoginWithoutError() {
        String viewName = controller.login(null, model);

        verify(model).addAttribute("loginError", false);
        verify(model).addAttribute("showLoginModal", true);
        assertEquals("login", viewName);
    }

    @Test
    void testShowRegistrationPageAddsForm() {
        when(model.containsAttribute("registrationForm")).thenReturn(false);

        String viewName = controller.showRegistrationPage(model);
        verify(model).addAttribute(eq("registrationForm"), any(RegistrationForm.class));
        assertEquals("register", viewName);
    }

    @Test
    void testShowRegistrationPageWhenFormExists() {
        RegistrationForm existingForm = new RegistrationForm();
        when(model.containsAttribute("registrationForm")).thenReturn(true);
        String viewName = controller.showRegistrationPage(model);

        verify(model, never()).addAttribute(eq("registrationForm"), any());
        assertEquals("register", viewName);
    }

    @Test
    void testRegisterUserWithValidationErrors() {
        RegistrationForm form = new RegistrationForm();
        form.setUsername("existingUser");
        form.setEmail("test@example.com");
        form.setPassword("secret");
        form.setFirstName("John");
        form.setLastName("Doe");
        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationForm");

        when(userRepository.findByUsername("existingUser")).thenReturn(new User());
        when(userRepository.findByEmail("test@example.com")).thenReturn(new User());

        String viewName = controller.registerUser(form, bindingResult, model);

        assertTrue(bindingResult.hasFieldErrors("username"));
        assertTrue(bindingResult.hasFieldErrors("email"));

        verify(model).addAttribute("registrationForm", form);
        assertEquals("register", viewName);
    }

    @Test
    void testRegisterUserSuccessful() {
        RegistrationForm form = new RegistrationForm();
        form.setUsername("newUser");
        form.setEmail("new@example.com");
        form.setPassword("secret");
        form.setFirstName("Alice");
        form.setLastName("Smith");

        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "registrationForm");

        when(userRepository.findByUsername("newUser")).thenReturn(null);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);

        when(passwordEncoder.encode("secret")).thenReturn("encodedSecret");

        String viewName = controller.registerUser(form, bindingResult, model);

        assertFalse(bindingResult.hasErrors());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("newUser", savedUser.getUsername());
        assertEquals("new@example.com", savedUser.getEmail());
        assertEquals("encodedSecret", savedUser.getPassword());
        assertEquals("Alice", savedUser.getFirstName());
        assertEquals("Smith", savedUser.getLastName());
        assertEquals("USER", savedUser.getRole());

        assertEquals("redirect:/login?registered=true", viewName);
    }
}
