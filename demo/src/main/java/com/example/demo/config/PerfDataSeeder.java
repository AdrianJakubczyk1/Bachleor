package com.example.demo.config;

import com.example.demo.persistent.model.Comment;
import com.example.demo.persistent.model.Post;
import com.example.demo.persistent.model.PostRating;
import com.example.demo.persistent.model.SchoolClass;
import com.example.demo.persistent.model.User;
import com.example.demo.persistent.repository.CommentRepository;
import com.example.demo.persistent.repository.PostRatingRepository;
import com.example.demo.persistent.repository.PostRepository;
import com.example.demo.persistent.repository.SchoolClassRepository;
import com.example.demo.persistent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Component
@Profile("perf")
public class PerfDataSeeder implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PostRepository postRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private PostRatingRepository postRatingRepository;
    @Autowired private SchoolClassRepository schoolClassRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // ==== tune here ====
    private static final int TEACHERS = 2000;
    private static final int USERS = 2000;   // USER role
    private static final int CLASSES_PER_TEACHER = 1;
    private static final int POSTS = 1000;
    private static final int COMMENTS = 5000;

    @Override
    public void run(String... args) {
        System.out.println("[PerfDataSeeder] Seeding for perf profile ...");

        // --- TEACHERS ---
        List<User> teachers = new ArrayList<>(TEACHERS);
        for (int i = 1; i <= TEACHERS; i++) {
            String u = "teacher" + i;
            User user = userRepository.findByUsername(u);
            if (user == null) {
                user = new User();
                user.setUsername(u);
                // IMPORTANT: your current password is "Password."
                user.setPassword(passwordEncoder.encode("Password."));
                user.setRole("TEACHER");
                user.setEmail(u + "@example.com");
                // save
                user = userRepository.save(user);
            }
            teachers.add(user);
        }

        // --- USERS (role USER) ---
        List<User> users = new ArrayList<>(USERS);
        for (int i = 1; i <= USERS; i++) {
            String u = "user" + i;
            User user = userRepository.findByUsername(u);
            if (user == null) {
                user = new User();
                user.setUsername(u);
                user.setPassword(passwordEncoder.encode("Password."));
                user.setRole("USER");
                user.setEmail(u + "@example.com");
                user = userRepository.save(user);
            }
            users.add(user);
        }

        // --- ADMIN ---
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Password."));
            admin.setRole("ADMIN");
            admin.setEmail("admin@example.com");
            userRepository.save(admin);
        }

        // --- CLASSES for each TEACHER (*** new block ***) ---
        // We ensure at least CLASSES_PER_TEACHER classes per teacher.
        // Works whether repo has create(...) or save(...). Adjust one line below if needed.
        Collection<SchoolClass> allClasses = schoolClassRepository.findAll();
        Map<Long, List<SchoolClass>> byTeacher =
                allClasses.stream().collect(Collectors.groupingBy(SchoolClass::getTeacherId));

        int createdClasses = 0;
        for (User t : teachers) {
            long tid = t.getId();
            List<SchoolClass> current = byTeacher.getOrDefault(tid, Collections.emptyList());
            int missing = Math.max(0, CLASSES_PER_TEACHER - current.size());
            for (int c = 1; c <= missing; c++) {
                SchoolClass sc = new SchoolClass();
                sc.setName("Perf Class " + t.getUsername() + " #" + c);
                sc.setDescription("Seeded for perf");
                sc.setContent("Intro");
                sc.setAutoApprove(true);
                sc.setTeacherId(tid);
                sc.setSignupDeadline(LocalDateTime.now().plusDays(30));

                // If your repo method is `save(sc)` instead of `create(sc)`, just change the next line:
                schoolClassRepository.create(sc);   // <-- change to save(sc) if your repo uses save
                createdClasses++;
            }
        }
        System.out.println("[PerfDataSeeder] Created classes: " + createdClasses);

        // --- POSTS ---
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        long existingPosts = postRepository.count(); // if you don’t have count(), skip this guard
        if (existingPosts < POSTS) {
            for (int i = 0; i < (POSTS - existingPosts); i++) {
                User teacher = teachers.get(rnd.nextInt(teachers.size()));
                Post p = new Post();
                p.setTitle("Perf Post " + (i + 1) + " by " + teacher.getUsername());
                p.setContent("Autogenerated content for performance testing. Index=" + (i + 1));
                p.setAuthor(teacher.getUsername());
                p.setViewCount(0);
                postRepository.save(p);
            }
        }

        // --- COMMENTS ---
        for (int i = 0; i < COMMENTS; i++) {
            // pick any post & user
            // If you need actual post ids: consider postRepository.findAll() once and reuse list.
            // Assuming postRepository exposes findAll(); otherwise keep as-is if not needed.
            // (Leaving as-is because your JMeter plan comments during runtime.)
        }

        // --- RATINGS (unique user-per-post) ---
        Set<String> unique = new HashSet<>();
        int created = 0;
        System.out.println("[PerfDataSeeder] Done.");
    }
}
