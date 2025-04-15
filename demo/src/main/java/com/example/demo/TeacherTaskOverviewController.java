package com.example.demo;

import com.example.demo.data.TaskSubmissionOverview;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.repository.ClassSignUpRepository;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.LessonTaskRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeacherTaskOverviewController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonTaskRepository lessonTaskRepository;

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @GetMapping("/teacher/submissions")
    public String viewTaskSubmissions(Model model, Principal principal) {
        // Get the currently logged-in teacher using their username.
        // Using the default mechanism, obtain the teacher's user record from the repository.
        String username = principal.getName();
        // Assume your UserRepository returns a User with an id (teacherId)
        // You can call: User teacher = userRepository.findByUsername(username)
        // For this example, we’ll assume teacherId is resolved as follows:
        Long teacherId = getTeacherIdFromUsername(username);

        // Retrieve classes assigned to this teacher
        List<SchoolClass> teacherClasses = schoolClassRepository.findByTeacherId(teacherId);
        List<TaskSubmissionOverview> overviewList = new ArrayList<>();

        // Loop over classes
        for (SchoolClass schoolClass : teacherClasses) {
            // Get enrolled student count for this class
            int enrolled = classSignUpRepository.findBySchoolClassIdAndStatus(schoolClass.getId(), "APPROVED").size();
            // Get lessons for this class
            List<Lesson> lessons = lessonRepository.findBySchoolClassId(schoolClass.getId());
            for (Lesson lesson : lessons) {
                // For each lesson, get tasks
                List<LessonTask> tasks = lessonTaskRepository.findByLessonId(lesson.getId());
                for (LessonTask task : tasks) {
                    // Count submissions for this task
                    int submitted = taskSubmissionRepository.findByLessonTaskId(task.getId()).size();

                    // Calculate remaining time in seconds, if a due date is set
                    long secondsRemaining = 0;
                    if (task.getDueDate() != null) {
                        LocalDateTime now = LocalDateTime.now();
                        if (task.getDueDate().isAfter(now)) {
                            secondsRemaining = Duration.between(now, task.getDueDate()).getSeconds();
                        }
                    }

                    TaskSubmissionOverview overview = new TaskSubmissionOverview();
                    overview.setTask(task);
                    overview.setSchoolClass(schoolClass);
                    overview.setEnrolledStudents(enrolled);
                    overview.setSubmittedCount(submitted);
                    overview.setSecondsRemaining(secondsRemaining);

                    overviewList.add(overview);
                }
            }
        }
        model.addAttribute("overviewList", overviewList);
        return "teacherTaskOverview"; // points to teacherTaskOverview.html
    }

    // Replace this dummy method with your actual logic to retrieve teacherId from the username.
    private Long getTeacherIdFromUsername(String username) {
        // For demonstration, assume teacher "teacher1" has id=3, others id=4.
        return "teacher1".equalsIgnoreCase(username) ? 3L : 4L;
    }
}
