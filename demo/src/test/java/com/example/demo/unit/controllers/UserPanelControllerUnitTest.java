package com.example.demo.unit.controllers;

import com.example.demo.UserPanelController;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPanelControllerUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private TaskSubmissionRepository taskSubmissionRepository;

    @Mock
    private Model model;

    @InjectMocks
    private UserPanelController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testUserPanel_PrincipalProvided() {
        String username = "testUser";
        Principal principal = () -> username;

        User user = new User();
        user.setId(100L);
        user.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(user);

        ClassSignUp signup = new ClassSignUp();
        signup.setSchoolClassId(10L);
        List<ClassSignUp> signups = Collections.singletonList(signup);
        when(classSignUpRepository.findByUserId(user.getId())).thenReturn(signups);

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(10L);
        schoolClass.setName("Math");
        when(schoolClassRepository.findById(10L)).thenReturn(Optional.of(schoolClass));

        TaskSubmission submission = new TaskSubmission();
        submission.setId(200L);
        List<TaskSubmission> submissions = Collections.singletonList(submission);
        when(taskSubmissionRepository.findByUserId(user.getId())).thenReturn(submissions);

        String view = controller.userPanel(model, principal);

        verify(model).addAttribute("user", user);
        verify(model).addAttribute("userClasses", Collections.singletonList(schoolClass));
        verify(model).addAttribute("submissions", submissions);
        assertEquals("userPanel", view);
    }

    @Test
    void testUserPanel_PrincipalNull() {
        Principal principal = null;

        String view = controller.userPanel(model, principal);

        verifyNoInteractions(userRepository, classSignUpRepository, schoolClassRepository, taskSubmissionRepository);
        assertEquals("userPanel", view);
    }
}
