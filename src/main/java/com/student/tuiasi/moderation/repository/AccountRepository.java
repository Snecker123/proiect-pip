package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.ParentAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AccountRepository {

    private final List<ParentAccount> accounts = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public ParentAccount save(ParentAccount account) {
        accounts.add(account);
        return account;
    }

    public Optional<ParentAccount> findById(int id) {
        return accounts.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    public Optional<ParentAccount> findByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    public List<ParentAccount> findAll() {
        return new ArrayList<>(accounts);
    }

    public void deleteById(int id) {
        accounts.removeIf(a -> a.getId() == id);
    }

    public int generateId() {
        return idCounter.getAndIncrement();
    }
}