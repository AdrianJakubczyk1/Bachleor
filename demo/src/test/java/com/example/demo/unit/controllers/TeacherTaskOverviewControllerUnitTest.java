package com.example.demo.unit.controllers;

import com.example.demo.TeacherTaskOverviewController;
import com.example.demo.data.TaskSubmissionOverview;
import com.example.demo.persistent.model.ClassSignUp;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.LessonTaskRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import com.example.demo.persistent.repository.UserRepository;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherTaskOverviewControllerUnitTest {

    @Mock
    private SchoolClassRepository schoolClassRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private LessonTaskRepository lessonTaskRepository;

    @Mock
    private ClassSignUpRepository classSignUpRepository;

    @Mock
    private TaskSubmissionRepository taskSubmissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TeacherTaskOverviewController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testViewTaskSubmissions_TeacherNotFound() {
        String username = "nonexistentTeacher";
        Principal principal = () -> username;
        when(userRepository.findByUsername(username)).thenReturn(null);
        String view = controller.viewTaskSubmissions(model, principal);

        assertEquals("redirect:/login?error=userNotFound", view);
        verify(userRepository).findByUsername(username);
        // No other repository should be invoked.
        verifyNoInteractions(schoolClassRepository, classSignUpRepository, lessonRepository,
                lessonTaskRepository, taskSubmissionRepository);
    }

    @Test
    void testViewTaskSubmissions_Success() {
        String username = "teacher1";
        Principal principal = () -> username;
        User teacher = new User();
        teacher.setId(1L);
        teacher.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(teacher);
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setId(10L);
        schoolClass.setName("Algebra");
        List<SchoolClass> teacherClasses = Collections.singletonList(schoolClass);
        when(schoolClassRepository.findByTeacherId(teacher.getId())).thenReturn(teacherClasses);

        ClassSignUp cs1 = new ClassSignUp();
        ClassSignUp cs2 = new ClassSignUp();
        List<ClassSignUp> approvedSignups = Arrays.asList(cs1, cs2);
        when(classSignUpRepository.findBySchoolClassIdAndStatus(10L, "APPROVED"))
                .thenReturn(approvedSignups);

        Lesson lesson = new Lesson();
        lesson.setId(20L);
        lesson.setSchoolClassId(10L);
        List<Lesson> lessons = Collections.singletonList(lesson);
        when(lessonRepository.findBySchoolClassId(10L)).thenReturn(lessons);

        LessonTask task = new LessonTask();
        task.setId(30L);
        LocalDateTime futureDueDate = LocalDateTime.now().plusSeconds(1000);
        task.setDueDate(futureDueDate);
        List<LessonTask> tasks = Collections.singletonList(task);
        when(lessonTaskRepository.findByLessonId(20L)).thenReturn(tasks);
        TaskSubmission sub1 = new TaskSubmission();
        TaskSubmission sub2 = new TaskSubmission();
        TaskSubmission sub3 = new TaskSubmission();
        List<TaskSubmission> submissions = Arrays.asList(sub1, sub2, sub3);
        when(taskSubmissionRepository.findByLessonTaskId(30L)).thenReturn(submissions);
        String view = controller.viewTaskSubmissions(model, principal);

        ArgumentCaptor<List<TaskSubmissionOverview>> captor =
                ArgumentCaptor.forClass(List.class);
        verify(model).addAttribute(eq("overviewList"), captor.capture());
        List<TaskSubmissionOverview> overviewList = captor.getValue();

        assertEquals(1, overviewList.size());
        TaskSubmissionOverview overview = overviewList.get(0);
        assertEquals(task, overview.getTask());
        assertEquals(schoolClass, overview.getSchoolClass());
        assertEquals(2, overview.getEnrolledStudents());
        assertEquals(3, overview.getSubmittedCount());
        long expectedRemaining = Duration.between(LocalDateTime.now(), futureDueDate).getSeconds();
        assertTrue(Math.abs(overview.getSecondsRemaining() - expectedRemaining) < 3,
                "Seconds remaining should be approximately " + expectedRemaining);
        assertEquals("teacherTaskOverview", view);
    }
}
