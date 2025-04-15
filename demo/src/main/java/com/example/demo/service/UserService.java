package com.example.demo.service;

import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable("usernameCheck")
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username) != null;
    }

    @Cacheable("emailCheck")
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }
}

