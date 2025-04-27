package com.example.demo.persistent.repository;


import com.example.demo.persistent.model.ClassSignUp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ClassSignUpRepository extends CrudRepository<ClassSignUp, Long> {

    List<ClassSignUp> findBySchoolClassId(Long schoolClassId);

    List<ClassSignUp> findBySchoolClassIdAndStatus(Long schoolClassId, String status);

    List<ClassSignUp> findByUserId(Long userId);

    Optional<ClassSignUp> findBySchoolClassIdAndUserId(Long schoolClassId, Long userId);


    List<ClassSignUp> findByStatus(String status);

    long countBySchoolClassIdAndUserId(Long schoolClassId, Long userId);
}
