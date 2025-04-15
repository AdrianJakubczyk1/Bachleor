package com.example.demo;

import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.repository.PostRepository;
import java.security.Principal;
import java.util.List;

import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.demo.persistent.model.User;


@Controller
@RequestMapping("/teacher/posts")
public class TeacherPostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    // List posts by the teacher.
    @GetMapping
    public String listTeacherPosts(Model model, Principal principal) {

        String username = principal.getName();

        User user = userRepository.findByUsername(username);

        List<Post> posts = postRepository.findByAuthor(username);
        model.addAttribute("posts", posts);
        return "teacherPosts"; // Resolves to teacherPosts.html template
    }

    // Show form to add a new post
    @GetMapping("/new")
    public String showAddPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "teacherPostForm"; // teacherPostForm.html
    }

    // Process new post form submission
    @PostMapping("/new")
    public String addPost(@ModelAttribute Post post, Principal principal) {
        post.setAuthor(principal.getName());  // assuming Post entity has an "author" field
        postRepository.save(post);
        return "redirect:/teacher/posts";
    }

    // Show form to edit an existing post (validate that post.author equals current teacher)
    @GetMapping("/{id}/edit")
    public String showEditPostForm(@PathVariable Long id, Model model, Principal principal) {
        Post post = postRepository.findById(id).orElse(null);
        if (post == null || !post.getAuthor().equals(principal.getName())) {
            return "redirect:/teacher/posts";
        }
        model.addAttribute("post", post);
        return "teacherPostForm";
    }

    // Process editing the post
    @PostMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, @ModelAttribute Post post, Principal principal) {
        post.setId(id);
        post.setAuthor(principal.getName());
        postRepository.save(post);
        return "redirect:/teacher/posts";
    }
}
