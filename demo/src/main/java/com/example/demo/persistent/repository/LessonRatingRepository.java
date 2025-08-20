package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.LessonRating;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class LessonRatingRepository {
    private final IMap<Long, LessonRating> map;
    private final FlakeIdGenerator idGenerator;

    public LessonRatingRepository(HazelcastInstance hz) {
        this.map = hz.getMap("lesson_ratings");
        this.idGenerator = hz.getFlakeIdGenerator("lesson_ratings_id_seq");
        // You may want to add indexes on lessonId and userId for performance:
        // map.addIndex(IndexType.HASH, "lessonId");
        // map.addIndex(IndexType.HASH, "userId");
    }

    /** Create a new LessonRating with generated ID */
    public LessonRating create(LessonRating template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Optional<LessonRating> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    /** Find single by lessonId & userId */
    public Optional<LessonRating> findByLessonIdAndUserId(Long lessonId, Long userId) {
        return map.values()
                .stream()
                .filter(r -> lessonId.equals(r.getLessonId()) &&
                        userId.equals(r.getUserId()))
                .findFirst();
    }

    /** Find all ratings for a given lesson */
    public List<LessonRating> findByLessonId(Long lessonId) {
        return map.values()
                .stream()
                .filter(r -> lessonId.equals(r.getLessonId()))
                .collect(Collectors.toList());
    }

    /** Update or insert explicitly */
    public LessonRating save(LessonRating rating) {
        if (rating.getId() == null) {
            long newId = idGenerator.newId();
            rating.setId(newId);
        }
        map.put(rating.getId(), rating);
        return rating;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all ratings (use with caution on large data sets) */
    public Collection<LessonRating> findAll() {
        return map.values();
    }
}