package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.ChildAccount;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository pentru gestionarea conturilor de copii.
 * Stocheaza datele in memorie folosind o lista Java.
 * Arhitectura permite migrarea ulterioara la o baza de date
 * fara modificari in straturile Service sau Controller.
 */
@Repository
public class ChildAccountRepository {

    /** Lista interna care stocheaza conturile de copii. */
    private final List<ChildAccount> accounts = new ArrayList<>();

    /** Contor atomic pentru generarea id-urilor unice. */
    private final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Salveaza un cont de copil in lista.
     *
     * @param account contul de salvat
     * @return contul salvat
     */
    public ChildAccount save(ChildAccount account) {
        accounts.add(account);
        return account;
    }

    /**
     * Cauta un cont de copil dupa id.
     *
     * @param id identificatorul cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ChildAccount> findById(int id) {
        return accounts.stream()
                .filter(a -> a.getId() == id)
                .findFirst();
    }

    /**
     * Cauta un cont de copil dupa username.
     *
     * @param username numele de utilizator cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ChildAccount> findByUsername(String username) {
        return accounts.stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst();
    }

    /**
     * Returneaza toti copiii asociati unui parinte.
     *
     * @param parentId identificatorul parintelui
     * @return lista cu toti copiii parintelui
     */
    public List<ChildAccount> findByParentId(int parentId) {
        return accounts.stream()
                .filter(a -> a.getParentId() == parentId)
                .toList();
    }

    /**
     * Returneaza toate conturile de copii.
     *
     * @return o copie a listei cu toate conturile
     */
    public List<ChildAccount> findAll() {
        return new ArrayList<>(accounts);
    }

    /**
     * Sterge un cont de copil dupa id.
     *
     * @param id identificatorul contului de sters
     * @return true daca stergerea a reusit, false daca nu a fost gasit
     */
    public boolean deleteById(int id) {
        return accounts.removeIf(a -> a.getId() == id);
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