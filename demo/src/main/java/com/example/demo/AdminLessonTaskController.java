package com.example.demo;

import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.repository.LessonTaskRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/lessons/{lessonId}/tasks")
public class AdminLessonTaskController {

    @Autowired
    private LessonTaskRepository lessonTaskRepository;

    // Display list of tasks for a lesson and a link to add a new task.
    @GetMapping
    public String listTasks(@PathVariable Long lessonId, Model model) {
        List<LessonTask> tasks = lessonTaskRepository.findByLessonId(lessonId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("lessonId", lessonId);
        return "adminLessonTaskList"; // Corresponds to adminLessonTaskList.html
    }

    // Show form to create a new task for the lesson.
    @GetMapping("/new")
    public String newTask(@PathVariable Long lessonId, Model model) {
        model.addAttribute("lessonId", lessonId);
        return "adminLessonTaskForm"; // Corresponds to adminLessonTaskForm.html
    }

    // Process the creation of a new task.
    // For simplicity, the form includes a string representation of a due date.
    @PostMapping("/new")
    public String createTask(@PathVariable Long lessonId,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String dueDate, // Format: yyyy-MM-dd'T'HH:mm or similar
                             @RequestParam(required = false, defaultValue = "true") boolean solutionRequired) {
        LessonTask task = new LessonTask();
        task.setLessonId(lessonId);
        task.setTitle(title);
        task.setDescription(description);
        // Parse the due date string to LocalDateTime.
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            task.setDueDate(LocalDateTime.parse(dueDate, formatter));
        } catch (Exception e) {
            // Handle parsing error (e.g., set default due date or send an error message)
            e.printStackTrace();
            task.setDueDate(LocalDateTime.now().plusDays(7)); // default to 7 days later
        }
        task.setSolutionRequired(solutionRequired);
        lessonTaskRepository.save(task);
        return "redirect:/admin/lessons/" + lessonId + "/tasks";
    }
}
