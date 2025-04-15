package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.SchoolClass;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface SchoolClassRepository extends CrudRepository<SchoolClass, Long> {
    List<SchoolClass> findByTeacherId(Long teacherId);
    List<SchoolClass> findByTeacherIdIsNull();
}
