package com.example.demo;

import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        // If there's an error parameter, you can add it to model as needed.
        model.addAttribute("loginError", error != null);
        // Set the flag to show the login modal.
        model.addAttribute("showLoginModal", true);
        return "login"; // The login view which includes your modal via your layout or directly.
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        // Ensure the registrationForm attribute is present:
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/perform_register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                               BindingResult bindingResult, Model model) {
        // Check if username already exists
        if (userRepository.findByUsername(form.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.registrationForm", "Username is already taken");
        }
        // Check if email already exists
        if (userRepository.findByEmail(form.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.registrationForm", "Email is already in use");
        }

        // If there are validation errors, return back to the registration page with errors.
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", form);
            return "register";
        }

        // If validation passes, create and save the new user.
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setRole("USER");  // Default role for new users

        userRepository.save(user);

        // Redirect to login page with a flag indicating successful registration.
        return "redirect:/login?registered=true";
    }
}