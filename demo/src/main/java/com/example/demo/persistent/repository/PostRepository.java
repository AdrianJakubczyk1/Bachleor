package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.Post;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    List<Post> findByAuthor(String author);
    long count();
}
