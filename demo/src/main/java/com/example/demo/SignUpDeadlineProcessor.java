package com.example.demo;

import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SignUpDeadlineProcessor {

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    // Run every minute (60000 milliseconds); adjust the fixedDelay as needed
    @Scheduled(fixedDelay = 60000)
    public void processPendingSignUps() {
        List<ClassSignUp> pendingSignUps = classSignUpRepository.findByStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        for (ClassSignUp signUp : pendingSignUps) {
            // Retrieve the associated SchoolClass by ID
            SchoolClass schoolClass = schoolClassRepository.findById(signUp.getSchoolClassId());
            if (schoolClass != null) {
                // Check deadline…
                if (schoolClass.getSignupDeadline() != null
                        && now.isAfter(schoolClass.getSignupDeadline())) {
                    signUp.setStatus("REJECTED");
                    classSignUpRepository.save(signUp);
                }
            }
        }
    }
}