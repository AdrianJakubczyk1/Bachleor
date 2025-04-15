package com.example.demo.unit.controllers;

import com.example.demo.UserValidationController;
import com.example.demo.service.UserService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserValidationControllerUnitTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserValidationController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckUsername_Exists() {
        String username = "testUser";
        when(userService.usernameExists(username)).thenReturn(true);

        Map<String, Boolean> result = controller.checkUsername(username);

        assertNotNull(result);
        assertTrue(result.containsKey("exists"));
        assertTrue(result.get("exists"));
    }

    @Test
    void testCheckUsername_NotExists() {
        String username = "nonexistentUser";
        when(userService.usernameExists(username)).thenReturn(false);
        Map<String, Boolean> result = controller.checkUsername(username);
        assertNotNull(result);
        assertTrue(result.containsKey("exists"));
        assertFalse(result.get("exists"));
    }

    @Test
    void testCheckEmail_Exists() {
        String email = "test@example.com";
        when(userService.emailExists(email)).thenReturn(true);
        Map<String, Boolean> result = controller.checkEmail(email);

        assertNotNull(result);
        assertTrue(result.containsKey("exists"));
        assertTrue(result.get("exists"));
    }

    @Test
    void testCheckEmail_NotExists() {
        String email = "nonexistent@example.com";
        when(userService.emailExists(email)).thenReturn(false);

        Map<String, Boolean> result = controller.checkEmail(email);

        assertNotNull(result);
        assertTrue(result.containsKey("exists"));
        assertFalse(result.get("exists"));
    }
}
