package com.example.demo.persistent.repository;
import com.example.demo.persistent.model.User;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.IAtomicLong;
import com.hazelcast.flakeidgen.FlakeIdGenerator;
import com.hazelcast.map.IMap;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    private final IMap<Long, User> map;
    private final FlakeIdGenerator idGenerator;

    public UserRepository(HazelcastInstance hz) {
        this.map = hz.getMap("users");
        this.idGenerator = hz.getFlakeIdGenerator("users_id_seq");
        // Optional index:
        // map.addIndex(IndexType.HASH, "username");
        // map.addIndex(IndexType.HASH, "email");
        // map.addIndex(IndexType.HASH, "role");
    }

    public User create(User template) {
        long id = idGenerator.newId();
        template.setId(id);
        map.put(id, template);
        return template;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(map.get(id));
    }

    public User findByUsername(String username) {
        return map.values()
                .stream()
                .filter(u -> username.equals(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

    public User findByEmail(String email) {
        return map.values()
                .stream()
                .filter(u -> email.equals(u.getEmail()))
                .findFirst()
                .orElse(null);
    }

    public long countByRole(String role) {
        return map.values()
                .stream()
                .filter(u -> role.equals(u.getRole()))
                .count();
    }

    public User save(User user) {
        if (user.getId() == null) {
            long newId = idGenerator.newId();
            user.setId(newId);
        }
        map.put(user.getId(), user);
        return user;
    }

    public long count() {
        return map.size();
    }

    public void delete(Long id) {
        map.remove(id);
    }

    public Collection<User> findAll() {
        return map.values();
    }

    public void deleteAll() { map.clear(); }
}