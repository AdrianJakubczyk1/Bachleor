package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.CommentLike;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CommentLikeRepository extends CrudRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUsername(Long commentId, String username);
    int countByCommentId(Long commentId);
}
