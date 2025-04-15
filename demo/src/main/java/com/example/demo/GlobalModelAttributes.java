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

    /**
     * This method adds common attributes to every model.
     */
    @ModelAttribute
    public void addCommonAttributes(Model model, Principal principal, HttpSession session) {
        boolean loggedIn = (principal != null);
        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            String username = principal.getName();
            model.addAttribute("username", username);

            // First, try to retrieve the "isAdmin" flag from the session.
            Boolean isAdmin = (Boolean) session.getAttribute("isAdmin");

            // Fallback: if not found in the session, derive from the security context.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (isAdmin == null) {
                isAdmin = auth.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
            }
            model.addAttribute("isAdmin", isAdmin);
            boolean isTeacher = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> "ROLE_TEACHER".equals(grantedAuthority.getAuthority()));
            model.addAttribute("isTeacher", isTeacher);

            // Optionally, add the user ID to the model.
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