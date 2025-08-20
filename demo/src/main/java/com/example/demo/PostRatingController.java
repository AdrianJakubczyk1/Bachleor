package com.example.demo;

import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.PostRating;
import com.example.demo.persistent.repository.PostRatingRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostRatingController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostRatingRepository postRatingRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{postId}/rate")
    public String ratePost(@PathVariable Long postId,
                           @RequestParam short rating,
                           Principal principal) {
        if (rating < 0 || rating > 10) {
            return "redirect:/posts/" + postId + "?error=invalidRating";
        }

        String username = principal.getName();
        Optional<com.example.demo.persistent.model.User> optUser = Optional.ofNullable(userRepository.findByUsername(username));
        if (optUser.isEmpty()) {
            // User not found; this should not occur if user is logged in.
            return "redirect:/posts/" + postId + "?error=userNotFound";
        }
        Long userId = optUser.get().getId();

        Optional<PostRating> existingRating = postRatingRepository.findByPostIdAndUserId(postId, userId);
        if (existingRating.isPresent()) {
            return "redirect:/posts/" + postId + "?error=alreadyRated";
        }

        // Create a new rating record.
        PostRating newRating = new PostRating();
        newRating.setPostId(postId);
        newRating.setUserId(userId);
        newRating.setRating(rating);
        postRatingRepository.save(newRating);
        List<PostRating> ratingsForPost = postRatingRepository.findByPostId(postId);
        double avg = ratingsForPost.stream()
                .collect(Collectors.averagingInt(PostRating::getRating));

        Post post = postRepository.findById(postId);
        if (post != null) {
            post.setAvgRating(avg);
            postRepository.save(post);
        }
        return "redirect:/posts/" + postId;
    }
}
