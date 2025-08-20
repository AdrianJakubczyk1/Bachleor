package com.example.demo;

import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TeacherClassDetailsController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LessonRepository lessonRepository;



    @GetMapping("/teacher/classes/{classId}/students")
    public String viewClassStudents(@PathVariable Long classId, Model model, Principal principal) {
        // findById now returns SchoolClass or null
        SchoolClass schoolClass = schoolClassRepository.findById(classId);
        if (schoolClass == null) {
            return "redirect:/teacher/classes?error=classNotFound";
        }

        User teacher = userRepository.findByUsername(principal.getName());
        if (teacher == null) {
            return "redirect:/login";
        }
        if (schoolClass.getTeacherId() == null || !schoolClass.getTeacherId().equals(teacher.getId())) {
            return "redirect:/teacher/classes?error=notAuthorized";
        }

        List<ClassSignUp> signups = classSignUpRepository.findBySchoolClassId(schoolClass.getId());
        List<ClassSignUpDetail> signupDetails = new ArrayList<>();

        for (ClassSignUp su : signups) {
            // findById returns Optional<User>
            Optional<User> studentOpt = userRepository.findById(su.getUserId());
            if (studentOpt.isPresent()) {
                User student = studentOpt.get();
                ClassSignUpDetail detail = new ClassSignUpDetail();
                detail.setSignUpId(su.getId());
                detail.setStudentId(student.getId());
                detail.setFirstName(student.getFirstName());
                detail.setLastName(student.getLastName());
                detail.setStatus(su.getStatus());
                detail.setCreatedDate(su.getCreatedDate());
                signupDetails.add(detail);
            }
        }

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("signupDetails", signupDetails);

        // lessonRepository.findBySchoolClassId returns a List<Lesson>
        List<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);

        return "teacherClassStudents";
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/approve")
    public String approveSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        // findById now returns Optional<ClassSignUp>
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            // delete by ID, not by entity
            classSignUpRepository.delete(signupId);
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/reject")
    public String rejectSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            classSignUpRepository.delete(signupId);
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }
}
