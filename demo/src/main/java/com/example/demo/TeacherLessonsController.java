package com.example.demo;

import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private ClassSignUpRepository classSignUpRepository;

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
        List<ClassSignUp> signups = classSignUpRepository.findBySchoolClassId(classId);
        List<User> enrolledStudents = signups.stream()
                .map(su -> userRepository.findById(su.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        List<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
        model.addAttribute("lessons", lessons);
        model.addAttribute("schoolClass", schoolClass);
        model.addAttribute("enrolledStudents", enrolledStudents);
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
        lesson.setSchoolClassId(classId);

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
                LocalDate date = LocalDate.parse(solutionDueDateStr);
                lesson.setSolutionDueDate(date.atStartOfDay());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            lesson.setSolutionDueDate(null);
        }
        if (lesson.getSolutionRequired() == null) {
            lesson.setSolutionRequired(false);
        }

        lesson = lessonRepository.save(lesson);

        if (Boolean.TRUE.equals(lesson.getSolutionRequired())) {
            LessonTask task = new LessonTask();
            task.setLessonId(lesson.getId());
            task.setTitle("Task for " + lesson.getTitle());
            task.setDescription("Solution is required for this lesson.");
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
                    LocalDate date = LocalDate.parse(solutionDueDateStr);
                    lesson.setSolutionDueDate(date.atStartOfDay());
                } catch (Exception e) {
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