package com.example.demo;

import com.example.demo.ClassSignUpDetail;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/classes/{classId}/students")
public class AdminClassStudentsController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Display a page with:
     * - List of enrolled (or pending) students for a class,
     *   showing each student’s first and last name, status, and enrollment date.
     * - A form to add a new student enrollment.
     */
    @GetMapping
    public String viewClassStudents(@PathVariable Long classId, Model model) {
        Optional<SchoolClass> classOpt = schoolClassRepository.findById(classId);
        if (!classOpt.isPresent()) {
            return "redirect:/admin/classes?error=classNotFound";
        }
        SchoolClass schoolClass = classOpt.get();

        // Get all sign-ups (enrollments) for this class.
        List<ClassSignUp> signUps = classSignUpRepository.findBySchoolClassId(schoolClass.getId());
        List<ClassSignUpDetail> signUpDetails = new ArrayList<>();

        // For each sign-up, load the student details.
        for (ClassSignUp signUp : signUps) {
            Optional<User> userOpt = userRepository.findById(signUp.getUserId());
            if (userOpt.isPresent()) {
                User student = userOpt.get();
                ClassSignUpDetail detail = new ClassSignUpDetail();
                detail.setSignUpId(signUp.getId());
                detail.setStudentId(student.getId());
                detail.setFirstName(student.getFirstName());
                detail.setLastName(student.getLastName());
                detail.setStatus(signUp.getStatus());
                detail.setCreatedDate(signUp.getCreatedDate());
                signUpDetails.add(detail);
            }
        }

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("signUpDetails", signUpDetails);

        // Also, prepare a list of available students to add.
        // For this, retrieve all users with role "USER" and remove those already enrolled in the class.
        List<User> allStudents = new ArrayList<>();
        userRepository.findAll().forEach(u -> {
            if ("USER".equalsIgnoreCase(u.getRole())) {
                allStudents.add(u);
            }
        });
        // Filter out users already enrolled.
        List<User> availableStudents = new ArrayList<>();
        for (User student : allStudents) {
            boolean alreadyEnrolled = signUps.stream().anyMatch(s -> s.getUserId().equals(student.getId()));
            if (!alreadyEnrolled) {
                availableStudents.add(student);
            }
        }
        model.addAttribute("availableStudents", availableStudents);

        return "adminClassStudents"; // This template will display the list and provide add/remove actions.
    }

    /**
     * Remove a student's enrollment from a class.
     */
    @PostMapping("/{signUpId}/remove")
    public String removeStudent(@PathVariable Long classId, @PathVariable Long signUpId) {
        classSignUpRepository.deleteById(signUpId);
        return "redirect:/admin/classes/" + classId + "/students";
    }

    /**
     * Process adding a new student enrollment to a class.
     * The student to add is selected by ID from the availableStudents dropdown.
     */
    @PostMapping("/add")
    public String addStudent(@PathVariable Long classId, @RequestParam Long studentId) {
        // Check if enrollment already exists
        Optional<ClassSignUp> existing = classSignUpRepository.findBySchoolClassIdAndUserId(classId, studentId);
        if (existing.isEmpty()) {
            ClassSignUp newSignUp = new ClassSignUp();
            newSignUp.setSchoolClassId(classId);
            newSignUp.setUserId(studentId);
            // Depending on your business rule, you may set new enrollments as APPROVED or PENDING
            newSignUp.setStatus("APPROVED");
            newSignUp.setCreatedDate(java.time.LocalDateTime.now());
            classSignUpRepository.save(newSignUp);
        }
        return "redirect:/admin/classes/" + classId + "/students";
    }
}