package com.example.demo.persistent.repository;


import com.example.demo.persistent.model.ClassSignUp;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClassSignUpRepository {
    private final IMap<Long, ClassSignUp> map;
    private final FlakeIdGenerator idGenerator;

    public ClassSignUpRepository(HazelcastInstance hz) {
        this.map = hz.getMap("class_signups");
        this.idGenerator = hz.getFlakeIdGenerator("class_signups_id_seq");
    }

    /** Create a new ClassSignUp with generated ID */
    public ClassSignUp create(ClassSignUp template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Optional<ClassSignUp> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public List<ClassSignUp> findBySchoolClassId(Long schoolClassId) {
        return map.values()
                .stream()
                .filter(cs -> schoolClassId.equals(cs.getSchoolClassId()))
                .collect(Collectors.toList());
    }

    /** Find all signups for a given classId *and* status */
    public List<ClassSignUp> findBySchoolClassIdAndStatus(Long schoolClassId, String status) {
        return map.values()
                .stream()
                .filter(cs -> schoolClassId.equals(cs.getSchoolClassId())
                        && status.equals(cs.getStatus()))
                .collect(Collectors.toList());
    }


    public Optional<ClassSignUp> findBySchoolClassIdAndUserId(Long schoolClassId, Long userId) {
        return map.values()
                .stream()
                .filter(cs -> schoolClassId.equals(cs.getSchoolClassId())
                        && userId.equals(cs.getUserId()))
                .findFirst();
    }
    public List<ClassSignUp> findByStatus(String status) {
        return map.values().stream()
                .filter(cs -> status.equals(cs.getStatus()))
                .collect(Collectors.toList());
    }

    public List<ClassSignUp> findByUserId(Long userId) {
        return map.values().stream()
                .filter(cs -> userId.equals(cs.getUserId()))
                .collect(Collectors.toList());
    }

    /** Update or insert explicitly */
    public ClassSignUp save(ClassSignUp cs) {
        if (cs.getId() == null) {
            long newId = idGenerator.newId();
            cs.setId(newId);
        }
        map.put(cs.getId(), cs);
        return cs;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all signups */
    public Collection<ClassSignUp> findAll() {
        return map.values();
    }

    /** Count total signups */
    public long count() {
        return map.size();
    }
}