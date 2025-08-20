package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.Comment;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CommentRepository {
    private final IMap<Long, Comment> map;
    private final FlakeIdGenerator idGenerator;

    public CommentRepository(HazelcastInstance hz) {
        this.map = hz.getMap("comments");
        this.idGenerator = hz.getFlakeIdGenerator("comments_id_seq");
    }

    /** Create a new Comment with generated ID */
    public Comment create(Comment template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Comment findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public Comment save(Comment c) {
        if (c.getId() == null) {
            long newId = idGenerator.newId();
            c.setId(newId);
        }
        map.put(c.getId(), c);
        return c;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    public long count() {
        return map.size();
    }

    public List<Comment> findByPostId(Long postId) {
        return map.values().stream()
                .filter(c -> postId.equals(c.getPostId()))
                .collect(Collectors.toList());
    }


    /** List all comments */
    public Collection<Comment> findAll() {
        return map.values();
    }
}