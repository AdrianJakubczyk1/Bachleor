package com.example.demo;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import com.example.demo.persistent.repository.PostRepository;
import java.security.Principal;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class HomeController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String home(Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "Educational Materials Sharing Platform");
        model.addAttribute("message", "Welcome to our educational app! Here you can share and explore educational materials.");
        Iterable<Post> posts = postRepository.findAll();
        model.addAttribute("posts", posts);

        return "index";
    }
}