package com.example.demo.config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CPSequenceInitializer {

    private final HazelcastInstance hazelcast;

    public CPSequenceInitializer(HazelcastInstance hazelcast) {
        this.hazelcast = hazelcast;
    }

    @PostConstruct
    public void init() {
        List<String> sequences = List.of(
                "class_signups_id_seq",
                "users_id_seq",
                "comments_id_seq",
                "comment_likes_id_seq",
                "lessons_id_seq",
                "lesson_tasks_id_seq",
                "lesson_ratings_id_seq",
                "posts_id_seq",
                "post_rating_id_seq",
                "school_classes_id_seq",
                "task_submissions_id_seq"
        );

        // Initialize FlakeIdGenerators (created on-demand)
        for (String name : sequences) {
            FlakeIdGenerator generator = hazelcast.getFlakeIdGenerator(name);
            generator.newId(); // force initial creation
        }
    }
}
