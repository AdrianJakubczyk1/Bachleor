package com.example.demo.persistent.repository;


import com.example.demo.persistent.model.Lesson;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    List<Lesson> findBySchoolClassId(Long schoolClassId);
}
