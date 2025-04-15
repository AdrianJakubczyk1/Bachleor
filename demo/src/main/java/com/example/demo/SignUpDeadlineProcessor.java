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
            Optional<SchoolClass> classOpt = schoolClassRepository.findById(signUp.getSchoolClassId());
            if (classOpt.isPresent()) {
                SchoolClass schoolClass = classOpt.get();
                // Check if signupDeadline is set and if the current time is after the deadline
                if (schoolClass.getSignupDeadline() != null && now.isAfter(schoolClass.getSignupDeadline())) {
                    // Automatically reject the pending sign-up
                    signUp.setStatus("REJECTED");
                    classSignUpRepository.save(signUp);
                }
            }
        }
    }
}