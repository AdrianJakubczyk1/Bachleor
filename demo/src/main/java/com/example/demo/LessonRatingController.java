package com.example.demo;
import com.example.demo.persistent.model.LessonRating;
import com.example.demo.persistent.repository.LessonRatingRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LessonRatingController {

    @Autowired
    private LessonRatingRepository lessonRatingRepository;

    @PostMapping("/lessons/{lessonId}/rate")
    public String rateLesson(@PathVariable Long lessonId,
                             @RequestParam int rating,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        // Here, assume a helper method to obtain userId from the principal.
        Long userId = getUserId(principal.getName());
        Optional<LessonRating> existing = lessonRatingRepository.findByLessonIdAndUserId(lessonId, userId);
        LessonRating lr = existing.orElseGet(LessonRating::new);
        lr.setLessonId(lessonId);
        lr.setUserId(userId);
        lr.setRating(rating);
        lr.setCreatedDate(LocalDateTime.now());
        lessonRatingRepository.save(lr);
        // Redirect back to a lesson detail page (or class detail page) as desired.
        return "redirect:/lessons/" + lessonId;
    }

    // Dummy helper: in a real app, use your user service
    private Long getUserId(String username) {
        return "admin".equalsIgnoreCase(username) ? 1L : 2L;
    }
}
