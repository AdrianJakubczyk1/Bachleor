package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.Comment;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    long count();
}