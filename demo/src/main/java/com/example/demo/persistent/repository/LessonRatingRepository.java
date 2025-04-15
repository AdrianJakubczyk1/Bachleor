package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.LessonRating;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface LessonRatingRepository extends CrudRepository<LessonRating, Long> {
    Optional<LessonRating> findByLessonIdAndUserId(Long lessonId, Long userId);
    List<LessonRating> findByLessonId(Long lessonId);
}