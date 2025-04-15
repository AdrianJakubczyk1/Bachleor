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

    @GetMapping
    public String listTasks(@PathVariable Long lessonId, Model model) {
        List<LessonTask> tasks = lessonTaskRepository.findByLessonId(lessonId);
        model.addAttribute("tasks", tasks);
        model.addAttribute("lessonId", lessonId);
        return "adminLessonTaskList"; // Corresponds to adminLessonTaskList.html
    }

    @GetMapping("/new")
    public String newTask(@PathVariable Long lessonId, Model model) {
        model.addAttribute("lessonId", lessonId);
        return "adminLessonTaskForm";
    }

    @PostMapping("/new")
    public String createTask(@PathVariable Long lessonId,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam String dueDate,
                             @RequestParam(required = false, defaultValue = "true") boolean solutionRequired) {
        LessonTask task = new LessonTask();
        task.setLessonId(lessonId);
        task.setTitle(title);
        task.setDescription(description);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        try {
            task.setDueDate(LocalDateTime.parse(dueDate, formatter));
        } catch (Exception e) {
            e.printStackTrace();
            task.setDueDate(LocalDateTime.now().plusDays(7));
        }
        task.setSolutionRequired(solutionRequired);
        lessonTaskRepository.save(task);
        return "redirect:/admin/lessons/" + lessonId + "/tasks";
    }
}
