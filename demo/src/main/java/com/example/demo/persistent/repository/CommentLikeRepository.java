package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.CommentLike;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public class CommentLikeRepository {
    private final IMap<Long, CommentLike> map;
    private final FlakeIdGenerator idGenerator;

    public CommentLikeRepository(HazelcastInstance hz) {
        // "comment_likes" → your map name
        this.map = hz.getMap("comment_likes");
        // "comment_likes_id_seq" → your sequence name
        this.idGenerator = hz.getFlakeIdGenerator("comment_likes_id_seq");
    }

    /** Create a new CommentLike with generated ID */
    public CommentLike create(CommentLike template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public CommentLike findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public CommentLike save(CommentLike cl) {
        if (cl.getId() == null) {
            long newId = idGenerator.newId();
            cl.setId(newId);
        }
        map.put(cl.getId(), cl);
        return cl;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    public int countByCommentId(Long commentId) {
        return (int) map.values().stream()
                .filter(cl -> commentId.equals(cl.getCommentId()))
                .count();
    }

    /** Check if a given user has liked a given comment */
    public Optional<CommentLike> findByCommentIdAndUsername(Long commentId, String username) {
        return map.values().stream()
                .filter(cl -> commentId.equals(cl.getCommentId())
                        && username.equals(cl.getUsername()))
                .findFirst();
    }

    /** List all likes (careful on very large maps) */
    public Collection<CommentLike> findAll() {
        return map.values();
    }
}
