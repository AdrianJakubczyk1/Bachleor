package com.example.demo.persistent.repository;

import com.example.demo.persistent.model.Post;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PostRepository {
    private final IMap<Long, Post> map;
    private final FlakeIdGenerator idGenerator;

    public PostRepository(HazelcastInstance hz) {
        this.map = hz.getMap("posts");
        this.idGenerator = hz.getFlakeIdGenerator("posts_id_seq");
        // Optionally add indexes:
        // map.addIndex(IndexType.HASH, "author");
        // map.addIndex(IndexType.SORTED, "title");
    }

    /** Create a new Post with generated ID */
    public Post create(Post template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    /** Read by ID */
    public Post findById(Long id) {
        return map.get(id);
    }

    /** Update or insert explicitly */
    public Post save(Post post) {
        if (post.getId() == null) {
            long newId = idGenerator.newId();
            post.setId(newId);
        }
        map.put(post.getId(), post);
        return post;
    }

    /** Delete by ID */
    public void delete(Long id) {
        map.remove(id);
    }

    /** List all posts */
    public Collection<Post> findAll() {
        return map.values();
    }

    /** Find all by author */
    public List<Post> findByAuthor(String author) {
        return map.values()
                .stream()
                .filter(p -> author.equals(p.getAuthor()))
                .collect(Collectors.toList());
    }

    /** Count total */
    public long count() {
        return map.size();
    }

    /** Title contains, case‑insensitive */
    public List<Post> findByTitleContainingIgnoreCase(String term) {
        String lower = term.toLowerCase();
        return map.values()
                .stream()
                .filter(p -> p.getTitle() != null &&
                        p.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
