package com.example.demo;


import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ClassesController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonTaskRepository lessonTaskRepository;

    @GetMapping("/classes")
    public String listClasses(Model model) {
        Iterable<SchoolClass> classes = schoolClassRepository.findAll();
        model.addAttribute("classes", classes);
        return "classes"; // classes.html
    }

    @GetMapping("/classes/{id}")
    public String classDetail(@PathVariable Long id, Model model, Principal principal) {
        // findById now returns SchoolClass or null
        SchoolClass schoolClass = schoolClassRepository.findById(id);
        if (schoolClass == null) {
            return "redirect:/classes";
        }
        model.addAttribute("schoolClass", schoolClass);

        boolean loggedIn = (principal != null);
        model.addAttribute("loggedIn", loggedIn);

        if (loggedIn) {
            String username = principal.getName();
            model.addAttribute("username", username);

            User user = userRepository.findByUsername(username);
            if (user != null) {
                Optional<ClassSignUp> signupOpt =
                        classSignUpRepository.findBySchoolClassIdAndUserId(id, user.getId());
                signupOpt.ifPresent(signup -> model.addAttribute("signup", signup));
            }
        }

        // Convert your Collection<Lesson> to a List for the template
        List<Lesson> lessons = lessonRepository.findBySchoolClassId(id)
                .stream()
                .collect(Collectors.toList());
        model.addAttribute("lessons", lessons);

        return "classDetail";
    }

    @PostMapping("/classes/{id}/signup")
    public String signUp(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName());
        if (user == null) {
            return "redirect:/login?error=userNotFound";
        }
        Long userId = user.getId();

        Optional<ClassSignUp> existing =
                classSignUpRepository.findBySchoolClassIdAndUserId(id, userId);
        if (existing.isEmpty()) {
            ClassSignUp signup = new ClassSignUp();
            signup.setSchoolClassId(id);
            signup.setUserId(userId);

            // findById now returns SchoolClass or null
            SchoolClass schoolClass = schoolClassRepository.findById(id);
            if (schoolClass != null && Boolean.TRUE.equals(schoolClass.getAutoApprove())) {
                signup.setStatus("APPROVED");
            } else {
                signup.setStatus("PENDING");
            }
            signup.setCreatedDate(LocalDateTime.now());
            classSignUpRepository.save(signup);
        }
        return "redirect:/classes/" + id;
    }


    @PostMapping("/classes/{classId}/lessons/{lessonId}/uploadSolution")
    public String uploadSolution(@PathVariable Long classId,
                                 @PathVariable Long lessonId,
                                 @RequestParam("solutionFile") MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();

            System.out.println("Uploaded file of size: " + fileBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/classes/" + classId;
    }

    @GetMapping("/classes/{id}/lessons")
    public String viewLessons(@PathVariable Long id, Model model, Principal principal) {
        // findById now returns SchoolClass or null
        SchoolClass schoolClass = schoolClassRepository.findById(id);
        if (schoolClass == null) {
            return "redirect:/classes?error=classNotFound";
        }
        model.addAttribute("schoolClass", schoolClass);

        // Retrieve lessons for the class
        List<Lesson> lessons = lessonRepository.findBySchoolClassId(id);
        model.addAttribute("lessons", lessons);

        Map<Long, Long> lessonTaskMapping = new HashMap<>();
        for (Lesson lesson : lessons) {
            List<LessonTask> tasks = lessonTaskRepository.findByLessonId(lesson.getId());
            if (!tasks.isEmpty()) {
                lessonTaskMapping.put(lesson.getId(), tasks.get(0).getId());
            }
        }
        model.addAttribute("lessonTaskMapping", lessonTaskMapping);

        return "classLessonsUser";
    }
}