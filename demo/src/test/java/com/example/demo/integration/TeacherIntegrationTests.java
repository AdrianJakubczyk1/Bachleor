package com.example.demo.integration;
import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;


@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {

                "spring.session.store-type=none",
                "spring.task.scheduling.enabled=false"
        }
)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WithMockUser(username = "t", roles = { "TEACHER" })
public class TeacherIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepo;
    @Autowired private SchoolClassRepository classRepo;
    @Autowired private ClassSignUpRepository signUpRepo;
    @Autowired private LessonRepository lessonRepo;
    @Autowired private LessonTaskRepository taskRepo;
    @Autowired private TaskSubmissionRepository submissionRepo;
    @Autowired private PostRepository postRepo;

    private User teacher;
    private User student;
    private SchoolClass available;
    private SchoolClass assigned;
    private Lesson lesson;
    private LessonTask task;
    private TaskSubmission submission;
    private Post post;

    @BeforeEach
    void setup() {
        submissionRepo.findAll().forEach( r-> submissionRepo.delete(r.getId()));
        taskRepo.findAll().forEach( r-> taskRepo.delete(r.getId()));
        lessonRepo.findAll().forEach( r-> lessonRepo.delete(r.getId()));
        signUpRepo.findAll().forEach( r-> signUpRepo.delete(r.getId()));
        postRepo.findAll().forEach( r->postRepo.delete(r.getId()));
        userRepo.findAll().forEach( r-> userRepo.delete(r.getId()));
        classRepo.findAll().forEach( r-> classRepo.delete(r.getId()));

        // Teacher user
        teacher = new User();
        teacher.setUsername("t");
        teacher.setPassword("p");
        teacher.setRole("TEACHER");
        userRepo.save(teacher);

        // Student user
        student = new User();
        student.setUsername("s");
        student.setPassword("p");
        student.setRole("USER");
        userRepo.save(student);

        // Available class
        available = new SchoolClass();
        available.setName("Avail");
        available.setTeacherId(teacher.getId());
        classRepo.save(available);

        // Assigned class
        assigned = new SchoolClass();
        assigned.setName("Assign");
        assigned.setTeacherId(teacher.getId());
        classRepo.save(assigned);

        // Lesson and task
        lesson = new Lesson();
        lesson.setSchoolClassId(assigned.getId());
        lesson.setTitle("L1");
        lesson.setDescription("Initial description");
        lesson.setContent("Initial content");
        lesson = lessonRepo.save(lesson);

        task = new LessonTask();
        task.setLessonId(lesson.getId());
        task.setTitle("T1");
        task.setDescription("D");
        task.setDueDate(LocalDateTime.now().plusDays(1));
        task.setSolutionRequired(true);
        task = taskRepo.save(task);

        // Submission
        submission = new TaskSubmission();
        submission.setLessonTaskId(task.getId());
        submission.setUserId(student.getId());
        submission.setSolutionText("sol");
        submission.setSolutionFilename("sol.txt");
        submission.setSolutionContentType("text/plain");
        submission.setSubmittedDate(LocalDateTime.now());
        submissionRepo.save(submission);

        // Post
        post = new Post();
        post.setTitle("PT");
        post.setContent("CNT");
        post.setAuthor(teacher.getUsername());
        postRepo.save(post);
    }

    @Test
    void panelAndClassesEndpoints() throws Exception {
        // Dashboard
        mockMvc.perform(get("/teacher"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("teacherId", teacher.getId()))
                .andExpect(view().name("teacherDashboard"));

        // Available
        mockMvc.perform(get("/teacher/availableClasses"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("availableClasses"));

        mockMvc.perform(post("/teacher/availableClasses/" + available.getId() + "/assign")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // My classes
        mockMvc.perform(get("/teacher/classes"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("classes"));
    }

    @Test
    void classDetailsAndStudentApproval() throws Exception {
        // Attempt details
        mockMvc.perform(get("/teacher/classes/" + assigned.getId() + "/students"))
                .andExpect(status().isOk());


        // Add a signup
        ClassSignUp su = new ClassSignUp();
        su.setSchoolClassId(assigned.getId());
        su.setUserId(student.getId());
        su.setStatus("PENDING");
        su.setCreatedDate(LocalDateTime.now());
        signUpRepo.save(su);

        // Approve
        mockMvc.perform(post("/teacher/classes/" + assigned.getId()
                        + "/students/" + su.getId() + "/approve")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(post("/teacher/classes/" + assigned.getId()
                        + "/students/" + su.getId() + "/reject")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void lessonManagement() throws Exception {
        // list
        mockMvc.perform(get("/teacher/classes/" + assigned.getId() + "/lessons"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("lessons", "schoolClass"));

        // new form
        mockMvc.perform(get("/teacher/classes/" + assigned.getId() + "/lessons/new"))
                .andExpect(status().isOk());

        // save
        mockMvc.perform(post("/teacher/classes/" + assigned.getId() + "/lessons/new")
                        .param("title", "X")
                        .param("description", "D")
                        .param("content", "C")
                        .param("solutionDueDateStr", "")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void postControllerEndpoints() throws Exception {
        // list and new
        mockMvc.perform(get("/teacher/posts"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("posts"));

        mockMvc.perform(get("/teacher/posts/new"))
                .andExpect(status().isOk());

        // add
        mockMvc.perform(post("/teacher/posts/new")
                        .param("title", "T")
                        .param("content", "C")
                        .with(csrf()))    // <-- CSRF
                .andExpect(status().is3xxRedirection());

        // edit form
        Post p = postRepo.findByAuthor(teacher.getUsername()).get(0);
        mockMvc.perform(get("/teacher/posts/" + p.getId() + "/edit"))
                .andExpect(status().isOk());

        // submit edits
        mockMvc.perform(post("/teacher/posts/" + p.getId() + "/edit")
                        .param("title", "T2")
                        .param("content", "C2")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
