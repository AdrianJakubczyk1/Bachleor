package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.TaskSubmission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TaskSubmissionRepository extends CrudRepository<TaskSubmission, Long> {
    List<TaskSubmission> findByLessonTaskId(Long lessonTaskId);
    Optional<TaskSubmission> findByLessonTaskIdAndUserId(Long lessonTaskId, Long userId);
    List<TaskSubmission> findByUserId(Long userId);
    List<TaskSubmission> findByGradeIsNull();
}
