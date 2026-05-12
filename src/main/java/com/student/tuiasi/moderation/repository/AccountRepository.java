package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.ParentAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository pentru gestionarea conturilor de parinti.
 * Stocheaza datele in memorie folosind o lista Java.
 * Arhitectura permite migrarea ulterioara la o baza de date
 * fara modificari in straturile Service sau Controller.
 */
@Repository
public class AccountRepository {

    /** Lista interna care stocheaza conturile de parinti. */
    private final List<ParentAccount> accounts = new ArrayList<>();

    /** Contor atomic pentru generarea id-urilor unice. */
    private final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Salveaza un cont de parinte in lista.
     *
     * @param account contul de salvat
     * @return contul salvat
     */
    public ParentAccount save(ParentAccount account) {
        accounts.add(account);
        return account;
    }

    /**
     * Cauta un cont de parinte dupa id.
     *
     * @param id identificatorul cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ParentAccount> findById(int id) {
        return accounts.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    /**
     * Cauta un cont de parinte dupa username.
     *
     * @param username numele de utilizator cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ParentAccount> findByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Returneaza toate conturile de parinti.
     *
     * @return o copie a listei cu toate conturile
     */
    public List<ParentAccount> findAll() {
        return new ArrayList<>(accounts);
    }

    /**
     * Sterge un cont de parinte dupa id.
     *
     * @param id identificatorul contului de sters
     */
    public void deleteById(int id) {
        accounts.removeIf(a -> a.getId() == id);
    }

    /**
     * Genereaza un id unic pentru un cont nou.
     *
     * @return urmatorul id disponibil
     */
    public int generateId() {
        return idCounter.getAndIncrement();
    }
}