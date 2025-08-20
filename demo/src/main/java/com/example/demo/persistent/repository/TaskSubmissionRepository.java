package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.TaskSubmission;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TaskSubmissionRepository {
    private final IMap<Long, TaskSubmission> map;
    private final FlakeIdGenerator idGenerator;

    public TaskSubmissionRepository(HazelcastInstance hz) {
        this.map = hz.getMap("task_submissions");
        this.idGenerator = hz.getFlakeIdGenerator("task_submissions_id_seq");
        // Optional indexes:
        // map.addIndex(IndexType.HASH, "lessonTaskId");
        // map.addIndex(IndexType.HASH, "userId");
        // map.addIndex(IndexType.HASH, "grade");
    }

    public TaskSubmission create(TaskSubmission template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    public Optional<TaskSubmission> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<TaskSubmission> findByLessonTaskId(Long lessonTaskId) {
        return map.values()
                .stream()
                .filter(s -> lessonTaskId.equals(s.getLessonTaskId()))
                .collect(Collectors.toList());
    }

    public Optional<TaskSubmission> findByLessonTaskIdAndUserId(Long lessonTaskId, Long userId) {
        return map.values()
                .stream()
                .filter(s -> lessonTaskId.equals(s.getLessonTaskId()) &&
                        userId.equals(s.getUserId()))
                .findFirst();
    }

    public List<TaskSubmission> findByUserId(Long userId) {
        return map.values()
                .stream()
                .filter(s -> userId.equals(s.getUserId()))
                .collect(Collectors.toList());
    }

    public List<TaskSubmission> findByGradeIsNull() {
        return map.values()
                .stream()
                .filter(s -> s.getGrade() == null)
                .collect(Collectors.toList());
    }

    public TaskSubmission save(TaskSubmission submission) {
        if ( submission.getId() == null) {
            long newId = idGenerator.newId();
            submission.setId(newId);
        }
        map.put( submission.getId(),  submission);
        return  submission;
    }

    public void delete(Long id) {
        map.remove(id);
    }

    public Collection<TaskSubmission> findAll() {
        return map.values();
    }
}
