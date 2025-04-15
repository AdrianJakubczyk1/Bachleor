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
        Optional<Lesson> lessonOpt = lessonRepository.findById(id);
        if (!lessonOpt.isPresent()) {
            return "redirect:/classes";
        }
        Lesson lesson = lessonOpt.get();
        Long classId = lesson.getSchoolClassId();

        boolean allowed = false;
        boolean loggedIn = principal != null;
        model.addAttribute("loggedIn", loggedIn);

        if (!loggedIn) {
            // If not logged in, force login.
            return "redirect:/login";
        }

        // Get current user from repository.
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/login";
        }
        Long userId = user.getId();

        // Check if the user is admin.
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> "ROLE_ADMIN".equals(grantedAuthority.getAuthority()));
        model.addAttribute("isAdmin", isAdmin);

        if (isAdmin) {
            allowed = true;
        } else {
            // Check if user is enrolled in the class with status "APPROVED"
            Optional<ClassSignUp> signupOpt = classSignUpRepository.findBySchoolClassIdAndUserId(classId, userId);
            if (signupOpt.isPresent() && "APPROVED".equalsIgnoreCase(signupOpt.get().getStatus())) {
                allowed = true;
            }
        }
        if (!allowed) {
            // If not allowed, redirect to class detail page (where they can sign up).
            return "redirect:/classes/" + classId;
        }

        model.addAttribute("lesson", lesson);

        // Compute average rating for the lesson.
        List<LessonRating> ratings = lessonRatingRepository.findByLessonId(id);
        double averageRating = ratings.stream().mapToInt(LessonRating::getRating).average().orElse(0.0);
        model.addAttribute("averageRating", averageRating);

        // Check if the current user has already rated this lesson.
        boolean userRated = lessonRatingRepository.findByLessonIdAndUserId(id, userId).isPresent();
        model.addAttribute("userRated", userRated);

        return "lessonDetail"; // Corresponds to lessonDetail.html
    }
}
