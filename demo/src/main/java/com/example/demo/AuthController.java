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
        model.addAttribute("loginError", error != null);
        model.addAttribute("showLoginModal", true);
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        return "register";
    }

    @PostMapping("/perform_register")
    public String registerUser(@Valid @ModelAttribute("registrationForm") RegistrationForm form,
                               BindingResult bindingResult, Model model) {
        if (userRepository.findByUsername(form.getUsername()) != null) {
            bindingResult.rejectValue("username", "error.registrationForm", "Username is already taken");
        }
        if (userRepository.findByEmail(form.getEmail()) != null) {
            bindingResult.rejectValue("email", "error.registrationForm", "Email is already in use");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("registrationForm", form);
            return "register";
        }
        User user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail());
        user.setFirstName(form.getFirstName());
        user.setLastName(form.getLastName());
        user.setRole("USER");

        userRepository.save(user);

        return "redirect:/login?registered=true";
    }
}