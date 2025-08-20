package com.example.demo;

import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.model.LessonRating;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.UserRepository;
import com.example.demo.persistent.repository.LessonRatingRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonRatingRepository lessonRatingRepository;

    @GetMapping("/lessons/{id}")
    public String lessonDetail(@PathVariable Long id, Model model, Principal principal) {
        // findById now returns Lesson or null
        Lesson lesson = lessonRepository.findById(id);
        if (lesson == null) {
            return "redirect:/classes";
        }
        Long classId = lesson.getSchoolClassId();

        boolean loggedIn = principal != null;
        model.addAttribute("loggedIn", loggedIn);
        if (!loggedIn) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName());
        if (user == null) {
            return "redirect:/login";
        }
        Long userId = user.getId();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        boolean allowed = isAdmin;
        if (!allowed) {
            // findBySchoolClassIdAndUserId returns Optional<ClassSignUp>
            Optional<ClassSignUp> signupOpt =
                    classSignUpRepository.findBySchoolClassIdAndUserId(classId, userId);
            if (signupOpt.isPresent() &&
                    "APPROVED".equalsIgnoreCase(signupOpt.get().getStatus())) {
                allowed = true;
            }
        }
        if (!allowed) {
            return "redirect:/classes/" + classId;
        }

        model.addAttribute("lesson", lesson);

        List<LessonRating> ratings = lessonRatingRepository.findByLessonId(id);
        double averageRating = ratings.stream()
                .mapToInt(LessonRating::getRating)
                .average()
                .orElse(0.0);
        model.addAttribute("averageRating", averageRating);

        boolean userRated = lessonRatingRepository
                .findByLessonIdAndUserId(id, userId)
                .isPresent();
        model.addAttribute("userRated", userRated);

        return "lessonDetail";
    }
}
