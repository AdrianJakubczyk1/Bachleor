package com.example.demo;

import com.example.demo.service.UserService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserValidationController {

    @Autowired
    private UserService userService;

    @GetMapping("/checkUsername")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.usernameExists(username);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }

    @GetMapping("/checkEmail")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return response;
    }
}
