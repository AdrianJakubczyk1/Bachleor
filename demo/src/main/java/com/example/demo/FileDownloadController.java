package com.example.demo;

import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.repository.PostRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FileDownloadController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/posts/{id}/attachment")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        // findById now returns Post or null
        Post post = postRepository.findById(id);
        if (post == null || post.getAttachment() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(post.getAttachmentContentType()));
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(post.getAttachmentFilename())
                .build());
        return new ResponseEntity<>(post.getAttachment(), headers, HttpStatus.OK);
    }
}