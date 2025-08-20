package com.example.demo.persistent.repository;


import com.example.demo.persistent.model.Lesson;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;

@Repository
public class LessonRepository {

    private final IMap<Long, Lesson> map;
    private final FlakeIdGenerator idGenerator;

    public LessonRepository(HazelcastInstance hz) {
        this.map = hz.getMap("lessons");
        this.idGenerator = hz.getFlakeIdGenerator("lessons_id_seq");
    }

    /** Create a new Lesson with generated ID */
    public Lesson create(Lesson template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Lesson findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public Lesson save(Lesson lesson) {
        if (lesson.getId() == null) {
            long newId = idGenerator.newId();
            lesson.setId(newId);
        }
        map.put(lesson.getId(), lesson);
        return lesson;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all lessons */
    public Collection<Lesson> findAll() {
        return map.values();
    }

    /** Custom finder: lessons by schoolClassId */
    public List<Lesson> findBySchoolClassId(Long schoolClassId) {
        return map.values().stream()
                .filter(l -> schoolClassId.equals(l.getSchoolClassId()))
                .collect(Collectors.toList());
    }
}

