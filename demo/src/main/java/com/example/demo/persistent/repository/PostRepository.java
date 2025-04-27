package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.Post;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import com.example.demo.persistent.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

public interface PostRepository extends CrudRepository<Post,Long>{
    List<Post> findByAuthor(String author);
    long count();
    List<Post> findByTitleContainingIgnoreCase(String title);
}
