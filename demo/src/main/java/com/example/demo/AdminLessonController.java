package com.example.demo;

import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/classes/{classId}/lessons")
public class AdminLessonController {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    // List all lessons for a given class
    @GetMapping
    public String listLessons(@PathVariable Long classId, Model model) {
        List<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);
        model.addAttribute("classId", classId);
        return "adminLessonList"; // corresponds to adminLessonList.html
    }

    // Display form to create a new lesson
    @GetMapping("/new")
    public String newLesson(@PathVariable Long classId, Model model) {
        model.addAttribute("classId", classId);
        return "adminLessonForm"; // corresponds to adminLessonForm.html
    }

    // Handle submission of the new lesson form
    @PostMapping("/new")
    public String addLesson(@PathVariable Long classId,
                            @RequestParam String title,
                            @RequestParam String description,
                            @RequestParam String content,
                            @RequestParam(value = "file", required = false) MultipartFile file) {
        Lesson lesson = new Lesson();
        lesson.setSchoolClassId(classId);
        lesson.setTitle(title);
        lesson.setDescription(description);
        lesson.setContent(content);
        if (file != null && !file.isEmpty()) {
            try {
                lesson.setAttachment(file.getBytes());
                lesson.setAttachmentFilename(file.getOriginalFilename());
                lesson.setAttachmentContentType(file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        lessonRepository.save(lesson);
        return "redirect:/admin/classes/" + classId + "/lessons";
    }
}

