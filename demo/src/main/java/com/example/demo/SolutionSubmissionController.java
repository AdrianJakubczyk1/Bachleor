package com.example.demo;
import org.springframework.ui.Model;
import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/classes/{classId}/lessons/{lessonId}/tasks/{taskId}/sendSolution/")
public class SolutionSubmissionController {

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/new")
    public String showSolutionForm(@PathVariable Long classId,
                                   @PathVariable Long lessonId,
                                   @PathVariable Long taskId,
                                   Model model) {
        TaskSubmission submission = new TaskSubmission();
        submission.setLessonTaskId(lessonId);
        model.addAttribute("submission", submission);
        model.addAttribute("classId", classId);
        model.addAttribute("lessonId", lessonId);
        model.addAttribute("taskId", taskId);
        return "solutionSubmissionForm";
    }

    @PostMapping("/new")
    public String submitSolution(@PathVariable Long classId,
                                 @PathVariable Long lessonId,
                                 @PathVariable Long taskId,
                                 @ModelAttribute TaskSubmission submission,
                                 @RequestParam(value="fileUpload", required=false) MultipartFile file,
                                 @RequestParam(value="solutionText", required=false) String solutionText,
                                 Principal principal) {
        submission.setLessonTaskId(taskId);
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName());
            if (user != null) {
                submission.setUserId(user.getId());
            }
        }

        if (file != null && !file.isEmpty()) {
            try {
                submission.setSolutionFile(file.getBytes());
                submission.setSolutionFilename(file.getOriginalFilename());
                submission.setSolutionContentType(file.getContentType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        submission.setSolutionText(solutionText);
        submission.setSubmittedDate(LocalDateTime.now());
        submission.setGrade(null);
        submission.setTeacherComments(null);

        taskSubmissionRepository.save(submission);

        return "redirect:/classes/" + classId + "/lessons";
    }
}