package com.example.demo;

import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teacher/availableClasses")
public class TeacherAvailableClassesController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private UserRepository userRepository;

    // List classes with no teacher assigned
    @GetMapping
    public String listAvailableClasses(Model model, Principal principal) {
        // Fetch available classes
        List<SchoolClass> availableClasses = schoolClassRepository.findByTeacherIdIsNull();
        model.addAttribute("availableClasses", availableClasses);
        return "teacherAvailableClasses"; // teacherAvailableClasses.html
    }

    // Allow teacher to self assign to a class that has no teacher assigned
    @PostMapping("/{id}/assign")
    public String assignSelfToClass(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        User teacher = userRepository.findByUsername(username);
        if (teacher == null) {
            return "redirect:/login";
        }
        Long teacherId = teacher.getId();
        Optional<SchoolClass> classOpt = schoolClassRepository.findById(id);
        if (classOpt.isPresent()) {
            SchoolClass cls = classOpt.get();
            if (cls.getTeacherId() == null) {  // Only assign if no teacher is assigned yet
                cls.setTeacherId(teacherId);
                schoolClassRepository.save(cls);
            }
        }
        return "redirect:/teacher/availableClasses";
    }
}
