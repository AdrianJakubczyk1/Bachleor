package com.example.demo;

import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.persistent.model.User;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping("/admin/classes")
public class AdminClassesController {


    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;


    // List all classes
    @GetMapping
    public String listClasses(Model model) {
        model.addAttribute("classes", schoolClassRepository.findAll());
        return "adminClasses"; // adminClasses.html
    }

    @GetMapping("/{id}/assign")
    public String showAssignTeacherForm(@PathVariable Long id, Model model) {
        Optional<SchoolClass> optClass = schoolClassRepository.findById(id);
        if (optClass.isPresent()) {
            SchoolClass schoolClass = optClass.get();
            model.addAttribute("schoolClass", schoolClass);
            // List all teachers from the user repository (assume role equals "TEACHER")
            List<User> teachers = (List<User>) userRepository.findAll(); // Filter later or add a custom method!
            // For example, filter in Java:
            teachers.removeIf(u -> !"TEACHER".equalsIgnoreCase(u.getRole()));
            model.addAttribute("teachers", teachers);
            return "adminAssignTeacher";  // Corresponds to adminAssignTeacher.html
        }
        return "redirect:/admin/classes";
    }

    // Process the teacher assignment
    @PostMapping("/{id}/assign")
    public String assignTeacher(@PathVariable Long id, @RequestParam Long teacherId) {
        Optional<SchoolClass> optClass = schoolClassRepository.findById(id);
        if (optClass.isPresent()) {
            SchoolClass schoolClass = optClass.get();
            schoolClass.setTeacherId(teacherId);
            schoolClassRepository.save(schoolClass);
        }
        return "redirect:/admin/classes";
    }

    // Show form to add a new class
    @GetMapping("/new")
    public String showAddClassForm(@PathVariable(required=false) Long id,Model model) {
        model.addAttribute("schoolClass", new SchoolClass());
        SchoolClass schoolClass;
        if (id != null) {
            schoolClass = schoolClassRepository.findById(id).orElse(new SchoolClass());
        } else {
            schoolClass = new SchoolClass();
        }

        model.addAttribute("schoolClass", schoolClass);

        List<User> teachers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .filter(u -> "TEACHER".equalsIgnoreCase(u.getRole()))
                .collect(Collectors.toList());
        model.addAttribute("teachers", teachers);

        return "adminClassForm";
    }

    @PostMapping("/new")
    public String addClass(@ModelAttribute SchoolClass schoolClass,  @RequestParam(value="signupDeadlineStr", required=false) String signupDeadlineStr,  @RequestParam(value="hasDeadline", required=false) String hasDeadline) {

        if (hasDeadline == null || signupDeadlineStr == null || signupDeadlineStr.trim().isEmpty()) {
            schoolClass.setSignupDeadline(null);
        } else {
            try {
                LocalDateTime deadline = LocalDateTime.parse(signupDeadlineStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                schoolClass.setSignupDeadline(deadline);
            } catch (Exception e) {
                e.printStackTrace();
                schoolClass.setSignupDeadline(null);
            }
        }
        schoolClassRepository.save(schoolClass);
        return "redirect:/admin/classes";
    }

    // Show form to edit a class
    @GetMapping("/{id}/edit")
    public String showEditClassForm(@PathVariable Long id, Model model) {
        Optional<SchoolClass> opt = schoolClassRepository.findById(id);
        if (opt.isPresent()) {
            SchoolClass schoolClass;
            if (id != null) {
                schoolClass = schoolClassRepository.findById(id).orElse(new SchoolClass());
            } else {
                schoolClass = new SchoolClass();
            }

            model.addAttribute("schoolClass", schoolClass);

            // Retrieve all users from the repository and filter those with role "USER"
            List<User> allStudents = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                    .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                    .collect(Collectors.toList());
            List<ClassSignUp> enrollments = classSignUpRepository.findBySchoolClassId(schoolClass.getId());
            Set<Long> enrolledStudentIds = enrollments.stream()
                    .map(ClassSignUp::getUserId)
                    .collect(Collectors.toSet());
            List<User> availableStudents = allStudents.stream()
                    .filter(u -> !enrolledStudentIds.contains(u.getId()))
                    .collect(Collectors.toList());
            model.addAttribute("availableStudents", availableStudents);

            List<User> teachers = StreamSupport.stream(userRepository.findAll().spliterator(), false)
                    .filter(u -> "TEACHER".equalsIgnoreCase(u.getRole()))
                    .collect(Collectors.toList());
            model.addAttribute("teachers", teachers);
            model.addAttribute("schoolClass", opt.get());
            return "adminClassForm";
        }
        return "redirect:/admin/classes";
    }

    // Process edit class form submission
    @PostMapping("/{id}/edit")
    public String editClass(@PathVariable Long id, @ModelAttribute SchoolClass schoolClass, @RequestParam(value="signupDeadlineStr", required=false) String signupDeadlineStr, @RequestParam(value="hasDeadline", required=false) String hasDeadline,
                            @RequestParam(value = "enrolledStudentIds", required = false) List<Long> enrolledStudentIds) {
        schoolClass.setId(id);
        if (hasDeadline == null || signupDeadlineStr == null || signupDeadlineStr.trim().isEmpty()) {
            schoolClass.setSignupDeadline(null);
        } else {
            try {
                LocalDateTime deadline = LocalDateTime.parse(signupDeadlineStr, java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                schoolClass.setSignupDeadline(deadline);
            } catch (Exception e) {
                e.printStackTrace();
                schoolClass.setSignupDeadline(null);
            }
        }
        schoolClassRepository.save(schoolClass);

        if (enrolledStudentIds != null && !enrolledStudentIds.isEmpty()) {
            for (Long studentId : enrolledStudentIds) {
                Optional<ClassSignUp> existing = classSignUpRepository.findBySchoolClassIdAndUserId(id, studentId);
                if (existing.isEmpty()) {
                    ClassSignUp signUp = new ClassSignUp();
                    signUp.setSchoolClassId(id);
                    signUp.setUserId(studentId);
                    signUp.setStatus("APPROVED");
                    signUp.setCreatedDate(LocalDateTime.now());
                    classSignUpRepository.save(signUp);
                }
            }
        }
        return "redirect:/admin/classes";
    }

    // Delete class
    @PostMapping("/{id}/delete")
    public String deleteClass(@PathVariable Long id) {
        schoolClassRepository.deleteById(id);
        return "redirect:/admin/classes";
    }
}