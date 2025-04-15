package com.example.demo;

import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModelAttributes {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal, HttpSession session) {
        boolean loggedIn = (principal != null);
        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            String username = principal.getName();
            model.addAttribute("username", username);
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (isAdmin == null) {
                isAdmin = auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
            }
            model.addAttribute("isAdmin", isAdmin);
            boolean isTeacher = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> "ROLE_TEACHER".equals(grantedAuthority.getAuthority()));
            model.addAttribute("isTeacher", isTeacher);
            User user = userRepository.findByUsername(username);
            if (user != null) {
                model.addAttribute("userId", user.getId());
            }
        } else {
            model.addAttribute("isAdmin", false);
        }
    }

    @ModelAttribute("registrationForm")
    public RegistrationForm registrationForm() {
        return new RegistrationForm();
    }
}