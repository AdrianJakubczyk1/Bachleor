package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.PostRating;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PostRatingRepository extends CrudRepository<PostRating, Long> {
    Optional<PostRating> findByPostIdAndUserId(Long postId, Long userId);
    List<PostRating> findByPostId(Long postId);
}
