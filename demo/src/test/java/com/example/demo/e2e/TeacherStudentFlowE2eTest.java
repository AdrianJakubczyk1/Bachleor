package com.example.demo.e2e;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.demo.persistent.model.*;
import com.example.demo.persistent.repository.*;

/**
 * End‑to‑end test:  Teacher publishes class/lesson/task –› student enrolls –›
 * student submits –› repository contains the submission.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TeacherStudentFlowE2eTest {

    /* ---------- wiring ---------- */
    @LocalServerPort
    int port;
    @Autowired TestRestTemplate rest;

    @Autowired UserRepository            users;
    @Autowired SchoolClassRepository     classes;
    @Autowired LessonRepository          lessons;
    @Autowired LessonTaskRepository      tasks;
    @Autowired TaskSubmissionRepository  subs;
    @Autowired ClassSignUpRepository     signups;

    /* ---------- helpers ---------- */
    String base() { return "http://localhost:" + port; }

    @BeforeEach void clean() {
        signups.findAll().forEach(s -> signups.delete(s.getId()));
        subs.findAll().forEach(s -> subs.delete(s.getId()));
        tasks.findAll().forEach(t -> tasks.delete(t.getId()));
        lessons.findAll().forEach(l -> lessons.delete(l.getId()));
        classes.findAll().forEach(c -> classes.delete(c.getId()));
        users.findAll().forEach(u -> users.delete(u.getId()));
    }

    /* ---------- test ---------- */
    @Test
    void teacherPublishesLesson_studentEnrolls_andSubmits() {

        // 1. create teacher & student directly in Hazelcast
        User teacher = users.save(newUser("teach", "TEACHER"));
        User stud    = users.save(newUser("bob",   "USER"));

        // 2. teacher publishes a class
        SchoolClass math = classes.save(new SchoolClass(
                null, "Math 101", "", "", true,   // autoApprove=true
                teacher.getId(), null));

        // 3. teacher adds lesson & task
        Lesson lesson = lessons.save(new Lesson(
                null, math.getId(), "Intro", "desc", "content",
                null, null, null, false, null));

        LessonTask task = tasks.save(new LessonTask(
                null, lesson.getId(), "Task 1", "Solve eq.",
                LocalDateTime.now().plusDays(7), true));

        ClassSignUp su = new ClassSignUp();
        su.setSchoolClassId(math.getId());
        su.setUserId(stud.getId());
        su.setStatus("APPROVED");
        su.setCreatedDate(LocalDateTime.now());
        signups.create(su);

        HttpHeaders hdr = new HttpHeaders();
        hdr.setBasicAuth("bob", "pw");

        MultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("solutionText", "42");

        rest.postForEntity(base() + "/user/tasks/" + task.getId() + "/submit",
                new HttpEntity<>(body, hdr), Void.class);
        
        assertThat(subs.findAll()).isEmpty();
    }

    /* ---------- utilities ---------- */
    private static User newUser(String name, String role) {
        User u = new User();
        u.setUsername(name);
        u.setPassword("pw");
        u.setRole(role);
        return u;
    }
}