package com.example.demo;


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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Controller
@RequestMapping("/teacher/classes/{classId}/lessons")
public class TeacherLessonsController {

    private final SchoolClassRepository  schoolClassRepository;
    private final LessonRepository       lessonRepository;
    private final UserRepository         userRepository;
    private final LessonTaskRepository   lessonTaskRepository;
    private final ClassSignUpRepository  classSignUpRepository;

    public TeacherLessonsController(
            SchoolClassRepository schoolClassRepository,
            LessonRepository lessonRepository,
            UserRepository userRepository,
            LessonTaskRepository lessonTaskRepository,
            ClassSignUpRepository classSignUpRepository
    ) {
        this.schoolClassRepository = schoolClassRepository;
        this.lessonRepository      = lessonRepository;
        this.userRepository        = userRepository;
        this.lessonTaskRepository  = lessonTaskRepository;
        this.classSignUpRepository = classSignUpRepository;
    }

    private boolean isTeacherAssignedToClass(Long teacherId, SchoolClass schoolClass) {
        return schoolClass != null && teacherId.equals(schoolClass.getTeacherId());
    }

    @GetMapping
    public String listLessons(@PathVariable Long classId,
                              Model model,
                              Principal principal) {
        User teacher = userRepository.findByUsername(principal.getName());
        SchoolClass schoolClass = schoolClassRepository.findById(classId);
        if (!isTeacherAssignedToClass(teacher.getId(), schoolClass)) {
            return "redirect:/teacher/classes?error=notAuthorized";
        }

        Collection<ClassSignUp> signups = classSignUpRepository.findBySchoolClassId(classId);
        List<User> enrolledStudents = signups.stream()
                .map(su -> userRepository.findById(su.getUserId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        Collection<Lesson> lessons = lessonRepository.findBySchoolClassId(classId);
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
                             @RequestParam(value = "solutionDueDate", required = false) String solutionDueDateStr) throws IOException {

        lesson.setSchoolClassId(classId);
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            lesson.setAttachment(attachmentFile.getBytes());
            lesson.setAttachmentFilename(attachmentFile.getOriginalFilename());
            lesson.setAttachmentContentType(attachmentFile.getContentType());
        }

        if (solutionDueDateStr != null && !solutionDueDateStr.isBlank()) {
            lesson.setSolutionDueDate(LocalDate.parse(solutionDueDateStr).atStartOfDay());
        } else {
            lesson.setSolutionDueDate(null);
        }
        if (lesson.getSolutionRequired()==null) {
            lesson.setSolutionRequired(false);
        }

        Lesson saved = lessonRepository.save(lesson);

        if (Boolean.TRUE.equals(saved.getSolutionRequired())) {
            LessonTask task = new LessonTask();
            task.setLessonId(saved.getId());
            task.setTitle("Task for " + saved.getTitle());
            task.setDescription("Solution required");
            task.setDueDate(
                    saved.getSolutionDueDate()!=null
                            ? saved.getSolutionDueDate()
                            : LocalDate.now().plusWeeks(1).atStartOfDay()
            );
            task.setSolutionRequired(true);
            lessonTaskRepository.save(task);
        }

        return "redirect:/teacher/classes/" + classId + "/lessons";
    }

    @GetMapping("/{lessonId}/edit")
    public String editLessonForm(@PathVariable Long classId,
                                 @PathVariable Long lessonId,
                                 Model model) {
        Lesson lesson = lessonRepository.findById(lessonId);
        if (lesson==null || !lesson.getSchoolClassId().equals(classId)) {
            return "redirect:/teacher/classes/" + classId + "/lessons?error=notFound";
        }
        model.addAttribute("lesson", lesson);
        model.addAttribute("classId", classId);
        return "teacherLessonForm";
    }

    @PostMapping("/{lessonId}/edit")
    public String updateLesson(@PathVariable Long classId,
                               @PathVariable Long lessonId,
                               @ModelAttribute Lesson details,
                               @RequestParam(value = "attachmentFile", required = false) MultipartFile attachmentFile,
                               @RequestParam(value = "solutionDueDate", required = false) String solutionDueDateStr) throws IOException {

        Lesson lesson = lessonRepository.findById(lessonId);
        if (lesson != null) {
            lesson.setTitle(details.getTitle());
            lesson.setDescription(details.getDescription());
            if (attachmentFile != null && !attachmentFile.isEmpty()) {
                lesson.setAttachment(attachmentFile.getBytes());
                lesson.setAttachmentFilename(attachmentFile.getOriginalFilename());
                lesson.setAttachmentContentType(attachmentFile.getContentType());
            }
            if (solutionDueDateStr != null && !solutionDueDateStr.isBlank()) {
                lesson.setSolutionDueDate(LocalDate.parse(solutionDueDateStr).atStartOfDay());
            } else {
                lesson.setSolutionDueDate(null);
            }
            if (lesson.getSolutionRequired()==null) {
                lesson.setSolutionRequired(false);
            }
            lessonRepository.save(lesson);
        }
        return "redirect:/teacher/classes/" + classId + "/lessons";
    }

    @PostMapping("/{lessonId}/delete")
    public String deleteLesson(@PathVariable Long classId,
                               @PathVariable Long lessonId) {
        lessonRepository.delete(lessonId);
        return "redirect:/teacher/classes/" + classId + "/lessons";
    }
}