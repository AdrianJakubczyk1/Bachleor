package com.example.demo;

import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @GetMapping
    public String listUsers(Model model) {
        // Convert the Iterable to a List
        List<User> users = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        // Map that will hold user id -> comma-separated string of class names
        Map<Long, String> userClassesMap = new HashMap<>();

        for (User user : users) {
            // Retrieve sign-ups for this user
            List<ClassSignUp> signups = classSignUpRepository.findByUserId(user.getId());
            // Convert sign-ups to class names
            String classNames = signups.stream()
                    .map(signup -> schoolClassRepository.findById(signup.getSchoolClassId()))
                    .filter(Optional::isPresent)
                    .map(opt -> opt.get().getName())
                    .collect(Collectors.joining(", "));
            userClassesMap.put(user.getId(), classNames);
        }

        // Add the list of users and the classes mapping to the model
        model.addAttribute("users", users);
        model.addAttribute("userClassesMap", userClassesMap);
        return "adminUsers"; // This corresponds to adminUsers.html
    }

    @GetMapping("/new")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        List<SchoolClass> classes = StreamSupport.stream(schoolClassRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
        model.addAttribute("classes", classes);
        return "adminUserForm";
    }

    @PostMapping("/new")
    public String addUser(@ModelAttribute User user,
                          @RequestParam(value = "classIds", required = false) List<Long> classIds,   Model model) {

        model.addAttribute("errorMessage", "Username " + user.getUsername() + " already exists.");

        // Save the new user; ensure that user.id is populated (if using auto-generated ID)
        userRepository.save(user);

        if (classIds != null && !classIds.isEmpty()) {
            for (Long classId : classIds) {

                ClassSignUp signup = new ClassSignUp();
                signup.setUserId(user.getId());
                signup.setSchoolClassId(classId);
                signup.setStatus("APPROVED");
                signup.setCreatedDate(LocalDateTime.now());
                classSignUpRepository.save(signup);
            }
        }
        return "redirect:/admin/users"; // Redirect to the list of users
    }

    @GetMapping("/{id}/edit")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            model.addAttribute("user", user);

            // Retrieve all available classes from the persistent schoolClassRepository
            List<SchoolClass> classes = StreamSupport
                    .stream(schoolClassRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            model.addAttribute("classes", classes);

            // Retrieve the class IDs for classes this user is already enrolled in
            List<Long> assignedClasses = classSignUpRepository.findByUserId(user.getId())
                    .stream()
                    .map(ClassSignUp::getSchoolClassId)
                    .collect(Collectors.toList());
            model.addAttribute("assignedClasses", assignedClasses);
            return "adminUserForm";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/edit")
    public String editUser(@PathVariable Long id,
                           @ModelAttribute User user,
                           @RequestParam(value = "classIds", required = false) List<Long> classIds) {
        Optional<User> optExistingUser = userRepository.findById(id);
        if (optExistingUser.isEmpty()) {
            return "redirect:/admin/users?error=userNotFound";
        }
        User existingUser = optExistingUser.get();

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setRole(user.getRole());
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(existingUser);

        List<ClassSignUp> existingSignups = classSignUpRepository.findByUserId(id);
        for (ClassSignUp cs : existingSignups) {
            if (classIds == null || !classIds.contains(cs.getSchoolClassId())) {
                classSignUpRepository.delete(cs);
            }
        }
        if (classIds != null) {
            for (Long classId : classIds) {
                Optional<ClassSignUp> signupOpt = classSignUpRepository.findBySchoolClassIdAndUserId(classId, id);
                if (signupOpt.isEmpty()) {
                    ClassSignUp cs = new ClassSignUp();
                    cs.setUserId(id);
                    cs.setSchoolClassId(classId);
                    cs.setStatus("APPROVED");
                    cs.setCreatedDate(LocalDateTime.now());
                    classSignUpRepository.save(cs);
                }
            }
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}