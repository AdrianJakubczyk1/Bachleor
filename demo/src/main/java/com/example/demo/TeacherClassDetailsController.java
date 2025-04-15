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
        // Retrieve the class.
        Optional<SchoolClass> classOpt = schoolClassRepository.findById(classId);
        if (!classOpt.isPresent()) {
            return "redirect:/teacher/classes?error=classNotFound";
        }
        SchoolClass schoolClass = classOpt.get();

        // Retrieve teacher's user record to get the teacher's ID.
        User teacher = userRepository.findByUsername(principal.getName());
        if (teacher == null) {
            return "redirect:/login";
        }
        // Ensure the logged-in teacher is assigned to this class.
        if (schoolClass.getTeacherId() == null || !schoolClass.getTeacherId().equals(teacher.getId())) {
            return "redirect:/teacher/classes?error=notAuthorized";
        }

        // Get all sign-ups for the class (e.g. enrolled students).
        List<ClassSignUp> signups = classSignUpRepository.findBySchoolClassId(schoolClass.getId());

        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("classSignUps", signups);
        Iterable<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);

        // Get the list of enrolled students for this class.
        List<User> enrolledStudents = signups.stream()
                .map(signup -> userRepository.findById(signup.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        model.addAttribute("enrolledStudents", enrolledStudents);
        return "teacherClassStudents"; // This will be our template to list enrolled students.
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/approve")
    public String approveSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            // Remove the signup record once it is approved
            classSignUpRepository.delete(opt.get());
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }

    @PostMapping("/teacher/classes/{classId}/students/{signupId}/reject")
    public String rejectSignup(@PathVariable Long classId, @PathVariable Long signupId) {
        Optional<ClassSignUp> opt = classSignUpRepository.findById(signupId);
        if (opt.isPresent()) {
            // Remove the signup record once it is rejected
            classSignUpRepository.delete(opt.get());
        }
        return "redirect:/teacher/classes/" + classId + "/students";
    }
}
