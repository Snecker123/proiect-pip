package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.ChildAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ChildAccountRepository {

    private final List<ChildAccount> accounts = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public ChildAccount save(ChildAccount account) {
        accounts.add(account);
        return account;
    }

    public Optional<ChildAccount> findById(int id) {
        return accounts.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    public Optional<ChildAccount> findByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    public List<ChildAccount> findByParentId(int parentId) {
        return accounts.stream()
                .filter(a -> a.getParentId() == parentId)
                .toList();
    }

    public List<ChildAccount> findAll() {
        return new ArrayList<>(accounts);
    }

    public boolean deleteById(int id) {
        return accounts.removeIf(a -> a.getId() == id);
    }

    public int generateId() {
        return idCounter.getAndIncrement();
    }
}