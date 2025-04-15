package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    long countByRole(String role);
}