package com.example.demo;

import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private PostRatingRepository postRatingRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/posts")
    public String listPosts(
            @RequestParam(value="q", required=false) String q,
            @RequestParam(value="page", defaultValue="0") int page,
            Model model) {

        List<Post> all = StreamSupport
                .stream(postRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (StringUtils.hasText(q)) {
            String lc = q.toLowerCase();
            all = all.stream()
                    .filter(p -> p.getTitle().toLowerCase().contains(lc))
                    .collect(Collectors.toList());
        }

        int pageSize = 6;
        int total = all.size();
        int totalPages = (total > 0)
                ? (int) Math.ceil((double) total / pageSize)
                : 1;

        if (page < 0) page = 0;
        if (page >= totalPages) page = totalPages - 1;

        int from = page * pageSize;
        int to   = Math.min(from + pageSize, total);
        List<Post> pageItems = from < to
                ? all.subList(from, to)
                : Collections.emptyList();

        model.addAttribute("posts", pageItems);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("searchTerm", q);
        return "post";
    }
    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable Long id, Model model, Principal principal) {

        Optional<Post> postOpt = postRepository.findById(id);
        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            post.setViewCount(post.getViewCount() + 1);

            boolean alreadyRated = false;
            if (principal != null) {
                String username = principal.getName();
                Optional<User> optUser = Optional.ofNullable(userRepository.findByUsername(username));
                if (optUser.isPresent()) {
                    Long userId = optUser.get().getId();
                    Optional<PostRating> existingRating = postRatingRepository.findByPostIdAndUserId(post.getId(), userId);
                    alreadyRated = existingRating.isPresent();
                    model.addAttribute("alreadyRated", alreadyRated);
                }
            }
            postRepository.save(post);

            model.addAttribute("post", post);

            List<Comment> comments = commentRepository.findByPostId(id);
            model.addAttribute("comments", comments);

            Map<Long, Integer> commentLikeCounts = new HashMap<>();
            Map<Long, Boolean> commentUserLiked = new HashMap<>();

            boolean loggedIn = (principal != null);
            model.addAttribute("loggedIn", loggedIn);
            if (loggedIn) {
                String username = principal.getName();
                model.addAttribute("username", username);
                for (Comment comment : comments) {
                    int count = commentLikeRepository.countByCommentId(comment.getId());
                    commentLikeCounts.put(comment.getId(), count);

                    boolean userLiked = commentLikeRepository.findByCommentIdAndUsername(comment.getId(), username).isPresent();
                    commentUserLiked.put(comment.getId(), userLiked);
                }
            }
            else {

                for (Comment comment : comments) {
                    int count = commentLikeRepository.countByCommentId(comment.getId());
                    commentLikeCounts.put(comment.getId(), count);
                    commentUserLiked.put(comment.getId(), false);
                }
            }
            model.addAttribute("commentLikeCounts", commentLikeCounts);
            model.addAttribute("commentUserLiked", commentUserLiked);

            return "postDetail"; // Corresponds to postDetail.html
        }
        return "redirect:/";
    }

    // Handle submission of a new comment
    @PostMapping("/posts/{id}/comments")
    public String addComment(@PathVariable Long id,
                             @RequestParam String text,
                             Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Comment comment = new Comment();
        comment.setPostId(id);
        comment.setUsername(principal.getName());
        comment.setText(text);
        comment.setLikes(0); // optional if you want to store a likes field
        comment.setCreatedDate(LocalDateTime.now());
        commentRepository.save(comment);
        return "redirect:/posts/" + id;
    }

    // Process a like for a comment; limit one like per user per comment
    @GetMapping("/comments/{commentId}/like")
    public String likeComment(@PathVariable Long commentId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Optional<CommentLike> existing = commentLikeRepository.findByCommentIdAndUsername(commentId, username);
        if (existing.isEmpty()) {
            CommentLike cl = new CommentLike();
            cl.setCommentId(commentId);
            cl.setUsername(username);
            commentLikeRepository.save(cl);
        }
        // Retrieve the post id for redirection
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (commentOpt.isPresent()) {
            Long postId = commentOpt.get().getPostId();
            return "redirect:/posts/" + postId;
        }
        return "redirect:/";
    }
}