package com.example.demo;

import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherPanelController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/teacher")
    public String teacherDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        model.addAttribute("teacherId", user.getId());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("title", "Teacher Panel");
        return "teacherDashboard";
    }
}