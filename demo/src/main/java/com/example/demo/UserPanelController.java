package com.example.demo;

import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import com.example.demo.persistent.repository.UserRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserPanelController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @GetMapping("/user/panel")
    public String userPanel(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            model.addAttribute("user", user);

            List<ClassSignUp> signups = classSignUpRepository.findByUserId(user.getId());
            System.out.println("Found " + signups.size() + " signups for user " + user.getId());

            // findById now returns SchoolClass or null
            List<SchoolClass> userClasses = signups.stream()
                    .map(su -> schoolClassRepository.findById(su.getSchoolClassId()))
                    .filter(sc -> sc != null)
                    .collect(Collectors.toList());
            model.addAttribute("userClasses", userClasses);

            List<TaskSubmission> submissions = taskSubmissionRepository.findByUserId(user.getId());
            model.addAttribute("submissions", submissions);
        }
        return "userPanel";
    }

    @PostMapping("/user/panel/update")
    public String updateProfile(@ModelAttribute("user") User form, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User existing = userRepository.findByUsername(username);
        if (existing != null) {
            existing.setFirstName(form.getFirstName());
            existing.setLastName(form.getLastName());
            // existing.setEmail(form.getEmail()); // uncomment if you want email editable
            userRepository.save(existing);
        }
        return "redirect:/user/panel";
    }
}
