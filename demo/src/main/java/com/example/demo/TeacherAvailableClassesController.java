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

    @GetMapping
    public String listAvailableClasses(Model model, Principal principal) {
        List<SchoolClass> availableClasses = schoolClassRepository.findByTeacherIdIsNull();
        model.addAttribute("availableClasses", availableClasses);
        return "teacherAvailableClasses"; // teacherAvailableClasses.html
    }

    @PostMapping("/{id}/assign")
    public String assignSelfToClass(@PathVariable Long id, Principal principal) {
        String username = principal.getName();
        User teacher = userRepository.findByUsername(username);
        if (teacher == null) {
            return "redirect:/login";
        }
        Long teacherId = teacher.getId();

        // findById now returns SchoolClass or null
        SchoolClass cls = schoolClassRepository.findById(id);
        if (cls != null && cls.getTeacherId() == null) {
            cls.setTeacherId(teacherId);
            schoolClassRepository.save(cls);
        }
        return "redirect:/teacher/availableClasses";
    }
}
