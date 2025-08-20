package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.SchoolClass;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SchoolClassRepository {

    private final IMap<Long, SchoolClass> map;
    private final FlakeIdGenerator idGenerator;

    public SchoolClassRepository(HazelcastInstance hz) {
        this.map = hz.getMap("school_classes");
        this.idGenerator = hz.getFlakeIdGenerator("school_classes_id_seq");
    }

    public SchoolClass create(SchoolClass schoolClass) {
        long id = idGenerator.newId();
        schoolClass.setId(id);
        map.put(id, schoolClass);
        return schoolClass;
    }

    /** Read by ID */
    public SchoolClass findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public SchoolClass save(SchoolClass sc) {
        if (sc.getId() == null) {
            long newId = idGenerator.newId();
            sc.setId(newId);
        }
        map.put(sc.getId(), sc);
        return sc;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all classes */
    public Collection<SchoolClass> findAll() {
        return map.values();
    }

    /** Find classes by teacherId */
    public List<SchoolClass> findByTeacherId(Long teacherId) {
        return map.values()
                .stream()
                .filter(c -> teacherId.equals(c.getTeacherId()))
                .collect(Collectors.toList());
    }

    /** Find classes with no teacher assigned */
    public List<SchoolClass> findByTeacherIdIsNull() {
        return map.values()
                .stream()
                .filter(c -> c.getTeacherId() == null)
                .collect(Collectors.toList());
    }
}
