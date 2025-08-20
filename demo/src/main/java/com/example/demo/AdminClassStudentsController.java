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

    @GetMapping
    public String viewClassStudents(@PathVariable Long classId, Model model) {
        // findById now returns SchoolClass or null
        SchoolClass schoolClass = schoolClassRepository.findById(classId);
        if (schoolClass == null) {
            return "redirect:/admin/classes?error=classNotFound";
        }

        List<ClassSignUp> signUps = classSignUpRepository.findBySchoolClassId(classId);
        List<ClassSignUpDetail> signUpDetails = new ArrayList<>();

        for (ClassSignUp signUp : signUps) {
            userRepository.findById(signUp.getUserId()).ifPresent(student -> {
                ClassSignUpDetail detail = new ClassSignUpDetail();
                detail.setSignUpId(signUp.getId());
                detail.setStudentId(student.getId());
                detail.setFirstName(student.getFirstName());
                detail.setLastName(student.getLastName());
                detail.setStatus(signUp.getStatus());
                detail.setCreatedDate(signUp.getCreatedDate());
                signUpDetails.add(detail);
            });
        }

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("signUpDetails", signUpDetails);
        return "adminClassStudents";
    }

    /**
     * Remove a student's enrollment from a class.
     */
    @PostMapping("/{signUpId}/remove")
    public String removeStudent(@PathVariable Long classId, @PathVariable Long signUpId) {
        classSignUpRepository.delete(signUpId);
        return "redirect:/admin/classes/" + classId + "/students";
    }

    @PostMapping("/add")
    public String addStudent(@PathVariable Long classId, @RequestParam Long studentId) {
        // Check if enrollment already exists
        Optional<ClassSignUp> existing = classSignUpRepository.findBySchoolClassIdAndUserId(classId, studentId);
        if (existing.isEmpty()) {
            ClassSignUp newSignUp = new ClassSignUp();
            newSignUp.setSchoolClassId(classId);
            newSignUp.setUserId(studentId);
            newSignUp.setStatus("APPROVED");
            newSignUp.setCreatedDate(java.time.LocalDateTime.now());
            classSignUpRepository.save(newSignUp);
        }
        return "redirect:/admin/classes/" + classId + "/students";
    }
}