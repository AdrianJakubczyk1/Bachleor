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
        Optional<SchoolClass> classOpt = schoolClassRepository.findById(id);
        if (classOpt.isPresent()) {
            SchoolClass schoolClass = classOpt.get();
            model.addAttribute("schoolClass", schoolClass);

            boolean loggedIn = (principal != null);
            model.addAttribute("loggedIn", loggedIn);

            if (loggedIn) {

                model.addAttribute("username", principal.getName());

                User user = userRepository.findByUsername(principal.getName());
                Long userId = user.getId();
                Optional<ClassSignUp> signupOpt = classSignUpRepository.findBySchoolClassIdAndUserId(id, userId);
                if (signupOpt.isPresent()) {
                    model.addAttribute("signup", signupOpt.get());
                }
            }

            if (schoolClass == null) {
                return "redirect:/classes";
            }
            model.addAttribute("schoolClass", schoolClass);

            List<Lesson> lessons = StreamSupport
                    .stream(lessonRepository.findBySchoolClassId(id).spliterator(), false)
                    .collect(Collectors.toList());
            model.addAttribute("lessons", lessons);


            return "classDetail";
        }
        return "redirect:/classes";
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
        Optional<ClassSignUp> existing = classSignUpRepository.findBySchoolClassIdAndUserId(id, userId);
        if (existing.isEmpty()) {
            ClassSignUp signup = new ClassSignUp();
            signup.setSchoolClassId(id);
            signup.setUserId(userId);

            SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
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
        SchoolClass schoolClass = schoolClassRepository.findById(id).orElse(null);
        if (schoolClass == null) {
            return "redirect:/classes?error=classNotFound";
        }
        model.addAttribute("schoolClass", schoolClass);

        // Retrieve lessons for the class (assumes a repository method exists)
        List<Lesson> lessons = lessonRepository.findBySchoolClassId(id);
        model.addAttribute("lessons", lessons);
        Map<Long, Long> lessonTaskMapping = new HashMap<>();
        for (Lesson lesson : lessons) {
            Optional<LessonTask> taskOpt = lessonTaskRepository.findByLessonId(lesson.getId())
                    .stream()
                    .findFirst();
            if (taskOpt.isPresent()){
                lessonTaskMapping.put(lesson.getId(), taskOpt.get().getId());
            }
        }
        model.addAttribute("lessonTaskMapping", lessonTaskMapping);
        return "classLessonsUser";
    }
}