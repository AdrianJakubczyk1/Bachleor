package com.example.demo;

import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.repository.SchoolClassRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;

@Controller
@RequestMapping("/teacher/classes")
public class TeacherClassController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping
    public String listMyClasses(Model model, Principal principal) {
        String username = principal.getName();
        User teacher = userRepository.findByUsername(username);
        if (teacher == null) {
            throw new RuntimeException("Teacher not found for username: " + username);
        }
        List<SchoolClass> myClasses = schoolClassRepository.findByTeacherId(teacher.getId());
        model.addAttribute("classes", myClasses);
        return "teacherClasses"; // teacherClasses.html – shows only assigned classes
    }


}
