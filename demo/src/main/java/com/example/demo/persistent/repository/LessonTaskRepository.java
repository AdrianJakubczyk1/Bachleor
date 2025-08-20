package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.LessonTask;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;

@Repository
public class LessonTaskRepository {
    private final IMap<Long, LessonTask> map;
    private final FlakeIdGenerator idGenerator;

    public LessonTaskRepository(HazelcastInstance hz) {
        this.map = hz.getMap("lesson_tasks");
        this.idGenerator = hz.getFlakeIdGenerator("lesson_tasks_id_seq");
        // Optionally add an index on lessonId:
        // map.addIndex(IndexType.HASH, "lessonId");
    }

    /** Create a new LessonTask with generated ID */
    public LessonTask create(LessonTask template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public LessonTask findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public LessonTask save(LessonTask task) {
        if (task.getId() == null) {
            long newId = idGenerator.newId();
            task.setId(newId);
        }
        map.put(task.getId(), task);
        return task;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all tasks */
    public Collection<LessonTask> findAll() {
        return map.values();
    }

    /** Find tasks by lessonId */
    public List<LessonTask> findByLessonId(Long lessonId) {
        return map.values()
                .stream()
                .filter(t -> lessonId.equals(t.getLessonId()))
                .collect(Collectors.toList());
    }
}