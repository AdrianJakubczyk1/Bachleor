package com.example.demo;

import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.repository.PostRepository;

import java.io.IOException;
import java.util.Optional;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/posts")
public class AdminPostController {

    @Autowired
    private PostRepository postRepository;

    // List all posts for admin management
    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "adminPosts"; // adminPosts.html view
    }

    @GetMapping("/new")
    public String showAddPostForm() {
        return "adminPostForm";
    }

    @PostMapping("/new")
    public String addPost(@RequestParam String title,
                          @RequestParam String content,
                          @RequestParam(value="thumbnailFile", required=false) MultipartFile thumbnailFile,
                          @RequestParam(value = "file", required = false) MultipartFile file, Principal principal) throws IOException {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(principal != null ? principal.getName() : "admin");

        if (file != null && !file.isEmpty()) {
            try {
                post.setAttachment(file.getBytes());
                post.setAttachmentFilename(file.getOriginalFilename());
                post.setAttachmentContentType(file.getContentType());
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

        // handle new thumbnail
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            post.setThumbnail(thumbnailFile.getBytes());
            post.setThumbnailFilename(thumbnailFile.getOriginalFilename());
            post.setThumbnailContentType(thumbnailFile.getContentType());
        }

        postRepository.save(post);
        return "redirect:/admin/posts";
    }

    // Show form to edit an existing post
    @GetMapping("/{id}/edit")
    public String editPost(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id);
        if (post != null) {
            model.addAttribute("post", post);
            return "adminPostEditForm";
        }
        return "redirect:/admin/posts";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @RequestParam String title,
                             @RequestParam String content,
                             @RequestParam(value="thumbnailFile", required=false) MultipartFile thumbnailFile,
                             @RequestParam(value = "file", required = false) MultipartFile file) {
        Post post = postRepository.findById(id);
        if (post != null) {
            post.setTitle(title);
            post.setContent(content);
            try {
                if (file != null && !file.isEmpty()) {
                    post.setAttachment(file.getBytes());
                    post.setAttachmentFilename(file.getOriginalFilename());
                    post.setAttachmentContentType(file.getContentType());
                }
                if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                    post.setThumbnail(thumbnailFile.getBytes());
                    post.setThumbnailFilename(thumbnailFile.getOriginalFilename());
                    post.setThumbnailContentType(thumbnailFile.getContentType());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            postRepository.save(post);
        }
        return "redirect:/admin/posts";
    }

    // Handle deletion of a post
    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postRepository.delete(id);
        return "redirect:/admin/posts";
    }
}