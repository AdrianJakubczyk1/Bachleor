package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.LessonTask;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface LessonTaskRepository extends CrudRepository<LessonTask, Long> {
    List<LessonTask> findByLessonId(Long lessonId);
}
