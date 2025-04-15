package com.example.demo;

import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class ApplicationToPublishScientificDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationToPublishScientificDataApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("user") == null) {
				User user = new User();
				user.setUsername("user");
				user.setPassword(passwordEncoder.encode("userpass"));
				user.setRole("USER");
				userRepository.save(user);
			}
			if (userRepository.findByUsername("admin") == null) {
				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("adminpass"));
				admin.setRole("ADMIN");
				userRepository.save(admin);
			}
		};
	}
}
