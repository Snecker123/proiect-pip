package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class PostRepository {

    private final List<Post> posts = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Post save(Post post) {
        posts.add(post);
        return post;
    }

    public Optional<Post> findById(int id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public List<Post> findByChildId(int childId) {
        return posts.stream()
                .filter(p -> p.getChildId() == childId)
                .toList();
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts);
    }

    public int generateId() {
        return idCounter.getAndIncrement();
    }
}