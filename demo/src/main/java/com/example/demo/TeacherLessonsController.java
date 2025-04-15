package com.example.demo;

import com.example.demo.persistent.model.Lesson;
import com.example.demo.persistent.model.LessonTask;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.LessonRepository;
import com.example.demo.persistent.repository.LessonTaskRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/teacher/classes/{classId}/lessons")
public class TeacherLessonsController {

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LessonTaskRepository lessonTaskRepository;

    private boolean isTeacherAssignedToClass(Long teacherId, SchoolClass schoolClass) {
        return schoolClass != null && teacherId.equals(schoolClass.getTeacherId());
    }

    @GetMapping
    public String listLessons(@PathVariable Long classId, Model model, Principal principal) {
        User teacher = userRepository.findByUsername(principal.getName());
        SchoolClass schoolClass = schoolClassRepository.findById(classId).orElse(null);
        if (!isTeacherAssignedToClass(teacher.getId(), schoolClass)) {
            return "redirect:/teacher/classes?error=notAuthorized";
        }

        List<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);
        model.addAttribute("schoolClass", schoolClass);
        return "teacherClassLessons";
    }

    @GetMapping("/new")
    public String addLessonForm(@PathVariable Long classId, Model model) {
        model.addAttribute("lesson", new Lesson());
        model.addAttribute("classId", classId);
        return "teacherLessonForm";
    }

    @PostMapping("/new")
    public String saveLesson(@PathVariable Long classId,
                             @ModelAttribute Lesson lesson,
                             @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile,
                             @RequestParam(value = "solutionDueDate", required = false) String solutionDueDateStr) {
        // Associate the lesson with the given class
        lesson.setSchoolClassId(classId);

        // Handle file attachment if provided
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            try {
                lesson.setAttachment(attachmentFile.getBytes());
                lesson.setAttachmentFilename(attachmentFile.getOriginalFilename());
                lesson.setAttachmentContentType(attachmentFile.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Manually parse the due date if provided
        if (solutionDueDateStr != null && !solutionDueDateStr.trim().isEmpty()) {
            try {
                // For input type "datetime-local", the value is typically in ISO_LOCAL_DATE_TIME format.
                // Adjust the parsing if you're using a different format.
                LocalDate date = LocalDate.parse(solutionDueDateStr);
                lesson.setSolutionDueDate(date.atStartOfDay());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // If no due date is provided, you may decide to leave it as null or set a default.
            lesson.setSolutionDueDate(null);
        }

        // Ensure the solutionRequired flag is not null.
        if (lesson.getSolutionRequired() == null) {
            lesson.setSolutionRequired(false);
        }

        // Save the lesson first to obtain its generated id.
        lesson = lessonRepository.save(lesson);

        // If a solution is required, create a LessonTask.
        if (Boolean.TRUE.equals(lesson.getSolutionRequired())) {
            LessonTask task = new LessonTask();
            task.setLessonId(lesson.getId());
            task.setTitle("Task for " + lesson.getTitle());
            task.setDescription("Solution is required for this lesson.");
            // Use lesson's due date if provided; otherwise, set a default (for example, one week from now).
            if (lesson.getSolutionDueDate() != null) {
                task.setDueDate(lesson.getSolutionDueDate());
            } else {
                task.setDueDate(LocalDate.now().plusWeeks(1).atStartOfDay());
            }
            task.setSolutionRequired(true);
            lessonTaskRepository.save(task);
        }

        return "redirect:/teacher/classes/" + classId + "/lessons";
    }

    @GetMapping("/{lessonId}/edit")
    public String editLessonForm(@PathVariable Long classId, @PathVariable Long lessonId, Model model) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson == null || !lesson.getSchoolClassId().equals(classId)) {
            return "redirect:/teacher/classes/" + classId + "/lessons?error=notFound";
        }
        model.addAttribute("lesson", lesson);
        model.addAttribute("classId", classId);
        return "teacherLessonForm";
    }

    @PostMapping("/{lessonId}/edit")
    public String updateLesson(@PathVariable Long classId, @PathVariable Long lessonId, @ModelAttribute Lesson lessonDetails,  @RequestParam(value = "attachment", required = false) MultipartFile attachmentFile, @RequestParam(value = "solutionDueDate", required = false) String solutionDueDateStr) {
        Lesson lesson = lessonRepository.findById(lessonId).orElse(null);
        if (lesson != null) {
            lesson.setTitle(lessonDetails.getTitle());
            lesson.setDescription(lessonDetails.getDescription());
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                try {
                    lesson.setAttachment(attachmentFile.getBytes());
                    lesson.setAttachmentFilename(attachmentFile.getOriginalFilename());
                    lesson.setAttachmentContentType(attachmentFile.getContentType());
                } catch (IOException e) {
                    // Log the error or handle it appropriately
                    e.printStackTrace();
                }
            }
            // Manually parse the date if provided
            if (solutionDueDateStr != null && !solutionDueDateStr.trim().isEmpty()) {
                try {
                    // For input type "date", the string is in the format "yyyy-MM-dd"
                    LocalDate date = LocalDate.parse(solutionDueDateStr);
                    // Convert to LocalDateTime (e.g., at start of day)
                    lesson.setSolutionDueDate(date.atStartOfDay());
                } catch (Exception e) {
                    // Handle parse error (log or set a default)
                    e.printStackTrace();
                }
            } else {
                lesson.setSolutionDueDate(null);
            }
            if (lesson.getSolutionRequired() == null) {
                lesson.setSolutionRequired(false);
            }
            lessonRepository.save(lesson);
        }
        return "redirect:/teacher/classes/" + classId + "/lessons";
    }

    @PostMapping("/{lessonId}/delete")
    public String deleteLesson(@PathVariable Long classId, @PathVariable Long lessonId) {
        lessonRepository.deleteById(lessonId);
        return "redirect:/teacher/classes/" + classId + "/lessons";
    }
}