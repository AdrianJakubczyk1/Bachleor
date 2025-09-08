package com.example.demo.unit.controllers;

import com.example.demo.ClassesController;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.LessonTaskRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClassesControllerUnitTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LessonTaskRepository lessonTaskRepository;

    @Mock
    private Model model;

    @InjectMocks
    private ClassesController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListClasses() {
        // Arrange
        SchoolClass class1 = new SchoolClass();
        class1.setId(1L);
        class1.setName("Math 101");
        SchoolClass class2 = new SchoolClass();
        class2.setId(2L);
        class2.setName("History 101");
        List<SchoolClass> classList = List.of(class1, class2);

        // Stub to return a List (which is a Collection)
        when(schoolClassRepository.findAll()).thenReturn(classList);

        // Act
        String viewName = controller.listClasses(model);

        // Assert
        verify(model).addAttribute("classes", classList);
        assertEquals("classes", viewName);
    }

    @Test
    void testClassDetailClassNotFound() {
        Long classId = 99L;
        when(schoolClassRepository.findById(classId)).thenReturn(null);

        // Act
        String viewName = controller.classDetail(classId, model, null);

        // Assert: should redirect.
        assertEquals("redirect:/classes", viewName);
    }

    @Test
    void testSignUpNotLoggedIn() {

        String redirect = controller.signup(1L, null);
        assertEquals("redirect:/classes", redirect);
    }

    @Test
    void testSignUpUserNotFound() {

        Principal principal = () -> "nonexistentUser";
        when(userRepository.findByUsername("nonexistentUser")).thenReturn(null);

        String redirect = controller.signup(1L, principal);
        assertEquals("redirect:/classes", redirect);
    }

    @Test
    void testSignUpNewRegistrationAutoApprove() {
        Long classId = 1L;
        Principal principal = () -> "student1";

        User user = new User();
        user.setId(20L);
        user.setUsername("student1");
        when(userRepository.findByUsername("student1")).thenReturn(user);

        when(classSignUpRepository.findBySchoolClassIdAndUserId(classId, 20L))
                .thenReturn(Optional.empty());

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(classId);
        schoolClass.setAutoApprove(true);
        when(schoolClassRepository.findById(classId)).thenReturn(schoolClass);

        String redirect = controller.signup(classId, principal);

        ArgumentCaptor<ClassSignUp> signupCaptor = ArgumentCaptor.forClass(ClassSignUp.class);
        verify(classSignUpRepository).save(signupCaptor.capture());
        ClassSignUp savedSignup = signupCaptor.getValue();
        assertEquals(classId, savedSignup.getSchoolClassId());
        assertEquals(20L, savedSignup.getUserId());
        assertEquals("APPROVED", savedSignup.getStatus());
        assertNotNull(savedSignup.getCreatedDate());

        assertEquals("redirect:/classes/" + classId, redirect);
    }

    @Test
    void testUploadSolution() throws IOException {
        Long classId = 1L;
        Long lessonId = 50L;

        byte[] fileContent = "sample solution".getBytes();
        MultipartFile file = new MockMultipartFile("solutionFile", "solution.txt", "text/plain", fileContent);

        String redirect = controller.uploadSolution(classId, lessonId, file);

        assertEquals("redirect:/classes/" + classId, redirect);
    }
    @Test
    void testViewLessons() {
        Long classId = 1L;

        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(classId);
        schoolClass.setName("Philosophy");
        when(schoolClassRepository.findById(classId)).thenReturn(schoolClass);

        Lesson lesson1 = new Lesson();
        lesson1.setId(100L);
        lesson1.setTitle("Lesson One");

        Lesson lesson2 = new Lesson();
        lesson2.setId(200L);
        lesson2.setTitle("Lesson Two");

        List<Lesson> lessons = Arrays.asList(lesson1, lesson2);
        when(lessonRepository.findBySchoolClassId(classId)).thenReturn(lessons);

        LessonTask task = new LessonTask();
        task.setId(500L);
        when(lessonTaskRepository.findByLessonId(100L)).thenReturn(Arrays.asList(task));
        when(lessonTaskRepository.findByLessonId(200L)).thenReturn(Collections.emptyList());

        String viewName = controller.viewLessons(classId, model, null);

        verify(model).addAttribute("schoolClass", schoolClass);
        verify(model).addAttribute("lessons", lessons);
        ArgumentCaptor<Map<Long, Long>> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(model).addAttribute(eq("lessonTaskMapping"), mapCaptor.capture());
        Map<Long, Long> lessonTaskMapping = mapCaptor.getValue();
        assertEquals(1, lessonTaskMapping.size());
        assertEquals(500L, lessonTaskMapping.get(100L));
        assertEquals("classLessonsUser", viewName);
    }

    @Test
    void testViewLessonsClassNotFound() {
        Long classId = 99L;
        when(schoolClassRepository.findById(classId)).thenReturn(null);

        String viewName = controller.viewLessons(classId, model, null);
        assertEquals("redirect:/classes?error=classNotFound", viewName);
    }
}
