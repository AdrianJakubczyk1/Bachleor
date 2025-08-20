package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.PostRating;
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
public class PostRatingRepository {
    private final IMap<Long, PostRating> map;
    private final FlakeIdGenerator idGenerator;

    public PostRatingRepository(HazelcastInstance hz) {
        this.map = hz.getMap("post_rating");
        this.idGenerator = hz.getFlakeIdGenerator("post_rating_id_seq");
        // Optionally add indexes for performance:
        // map.addIndex(IndexType.HASH, "postId");
        // map.addIndex(IndexType.HASH, "userId");
    }

    /** Create a new PostRating with generated ID */
    public PostRating create(PostRating template) {
        long id = idGenerator.newId();

        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Optional<PostRating> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    /** Find by composite keys */
    public Optional<PostRating> findByPostIdAndUserId(Long postId, Long userId) {
        return map.values()
                .stream()
                .filter(r -> postId.equals(r.getPostId()) && userId.equals(r.getUserId()))
                .findFirst();
    }

    /** Find all ratings for a post */
    public List<PostRating> findByPostId(Long postId) {
        return map.values()
                .stream()
                .filter(r -> postId.equals(r.getPostId()))
                .collect(Collectors.toList());
    }

    /** Update or insert explicitly */
    public PostRating save(PostRating rating) {
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

    /** List all ratings (use with caution if map is large) */
    public Collection<PostRating> findAll() {
        return map.values();
    }
}
