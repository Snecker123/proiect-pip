package com.student.tuiasi.moderation.repository;

import com.student.tuiasi.moderation.model.Post;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Repository pentru gestionarea postarilor.
 * Stocheaza datele in memorie folosind o lista Java.
 * Postarile nu pot fi sterse — istoricul complet este pastrat
 * pentru monitorizarea activitatii copiilor de catre parinti.
 */
@Repository
public class PostRepository {

    /** Lista interna care stocheaza postarile. */
    private final List<Post> posts = new ArrayList<>();

    /** Contor atomic pentru generarea id-urilor unice. */
    private final AtomicInteger idCounter = new AtomicInteger(1);

    /**
     * Salveaza o postare in lista.
     *
     * @param post postarea de salvat
     * @return postarea salvata
     */
    public Post save(Post post) {
        posts.add(post);
        return post;
    }

    /**
     * Cauta o postare dupa id.
     *
     * @param id identificatorul cautat
     * @return Optional cu postarea gasita sau Optional gol daca nu exista
     */
    public Optional<Post> findById(int id) {
        return posts.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    /**
     * Returneaza toate postarile unui copil.
     *
     * @param childId identificatorul copilului
     * @return lista cu toate postarile copilului
     */
    public List<Post> findByChildId(int childId) {
        return posts.stream()
                .filter(p -> p.getChildId() == childId)
                .toList();
    }

    /**
     * Returneaza toate postarile din sistem.
     *
     * @return o copie a listei cu toate postarile
     */
    public List<Post> findAll() {
        return new ArrayList<>(posts);
    }

    /**
     * Genereaza un id unic pentru o postare noua.
     *
     * @return urmatorul id disponibil
     */
    public int generateId() {
        return idCounter.getAndIncrement();
    }
}