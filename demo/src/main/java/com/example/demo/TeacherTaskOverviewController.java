package com.example.demo;

import com.example.demo.data.TaskSubmissionOverview;
import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;
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
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/teacher/submissions")
    public String viewTaskSubmissions(Model model, Principal principal) {
        String username = principal.getName();
        User teacher = userRepository.findByUsername(username);
        if (teacher == null) {
            return "redirect:/login?error=userNotFound";
        }
        Long teacherId = teacher.getId();

        List<SchoolClass> teacherClasses = schoolClassRepository.findByTeacherId(teacherId);
        List<TaskSubmissionOverview> overviewList = new ArrayList<>();

        for (SchoolClass schoolClass : teacherClasses) {
            int enrolled = classSignUpRepository.findBySchoolClassIdAndStatus(schoolClass.getId(), "APPROVED").size();
            List<Lesson> lessons = lessonRepository.findBySchoolClassId(schoolClass.getId());
            for (Lesson lesson : lessons) {
                List<LessonTask> tasks = lessonTaskRepository.findByLessonId(lesson.getId());
                for (LessonTask task : tasks) {
                    int submitted = taskSubmissionRepository.findByLessonTaskId(task.getId()).size();
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
        return "teacherTaskOverview";
    }
}
