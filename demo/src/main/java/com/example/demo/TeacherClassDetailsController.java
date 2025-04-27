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
        Optional<SchoolClass> classOpt = schoolClassRepository.findById(classId);
        if (!classOpt.isPresent()) {
            return "redirect:/teacher/classes?error=classNotFound";
        }
        SchoolClass schoolClass = classOpt.get();

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
            userRepository.findById(su.getUserId()).ifPresent(user -> {
                ClassSignUpDetail detail = new ClassSignUpDetail();
                detail.setSignUpId(su.getId());
                detail.setStudentId(user.getId());
                detail.setFirstName(user.getFirstName());
                detail.setLastName(user.getLastName());
                detail.setStatus(su.getStatus());
                detail.setCreatedDate(su.getCreatedDate());
                signupDetails.add(detail);
            });
        }

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("signupDetails", signupDetails);
        Iterable<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);

        return "teacherClassStudents";
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/approve")
    public String approveSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            classSignUpRepository.delete(opt.get());
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/reject")
    public String rejectSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            classSignUpRepository.delete(opt.get());
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }
}
