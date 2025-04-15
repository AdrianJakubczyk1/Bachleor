package com.example.demo;

import com.example.demo.persistent.model.TaskSubmission;
import com.example.demo.persistent.repository.TaskSubmissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/teacher/task-submissions")
public class TeacherTaskSubmissionController {

    @Autowired
    private TaskSubmissionRepository taskSubmissionRepository;

    @GetMapping
    public String listUngradedSubmissions(Model model) {
        List<TaskSubmission> ungradedSubmissions = taskSubmissionRepository.findByGradeIsNull();
        model.addAttribute("submissions", ungradedSubmissions);
        return "teacherSubmissions";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadSubmissionFile(@PathVariable Long id) {
        Optional<TaskSubmission> submissionOpt = taskSubmissionRepository.findById(id);
        if (submissionOpt.isPresent() && submissionOpt.get().getSolutionFile() != null) {
            TaskSubmission submission = submissionOpt.get();
            HttpHeaders headers = new HttpHeaders();
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            if (submission.getSolutionContentType() != null) {
                try {
                    mediaType = MediaType.parseMediaType(submission.getSolutionContentType());
                } catch (IllegalArgumentException ex) {
                    // keep default
                }
            }
            headers.setContentType(mediaType);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(submission.getSolutionFilename() != null
                            ? submission.getSolutionFilename()
                            : "solution_" + id + ".dat")
                    .build());
            return new ResponseEntity<>(submission.getSolutionFile(), headers, HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/{id}/grade")
    public String gradeSubmission(@PathVariable Long id,
                                  @RequestParam("grade") Double grade,
                                  @RequestParam("teacherComments") String teacherComments) {
        if (grade < 0 || grade > 10) {
            return "redirect:/teacher/task-submissions?error=invalidGrade";
        }
        Optional<TaskSubmission> submissionOpt = taskSubmissionRepository.findById(id);
        if (submissionOpt.isPresent()) {
            TaskSubmission submission = submissionOpt.get();
            submission.setGrade(grade);
            submission.setTeacherComments(teacherComments);
            taskSubmissionRepository.save(submission);
        }
        return "redirect:/teacher/task-submissions";
    }
}
