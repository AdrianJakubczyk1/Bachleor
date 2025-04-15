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

            List<SchoolClass> userClasses = signups.stream()
                    .map(signup -> schoolClassRepository.findById(signup.getSchoolClassId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            model.addAttribute("userClasses", userClasses);
            List<TaskSubmission> submissions = taskSubmissionRepository.findByUserId(user.getId());
            model.addAttribute("submissions", submissions);
        }
        return "userPanel";
    }
}
