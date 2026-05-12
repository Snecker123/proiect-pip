package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.model.Post;
import com.student.tuiasi.moderation.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviciu pentru gestionarea postarilor copiilor.
 * Coordoneaza fluxul complet de postare: verificarea existentei
 * copilului, moderarea automata a continutului, salvarea postarii
 * si notificarea parintelui in caz de continut blocat.
 */
@Service
public class PostService {

    /** Repository pentru accesul la datele postarilor. */
    private final PostRepository postRepository;

    /** Serviciu pentru moderarea automata a textului. */
    private final ModerationService moderationService;

    /** Serviciu pentru accesul la datele conturilor de copii. */
    private final ChildAccountService childAccountService;

    /** Serviciu pentru accesul la datele conturilor de parinti. */
    private final AccountService accountService;

    /** Serviciu pentru trimiterea notificarilor prin email. */
    private final NotificationService notificationService;

    /**
     * Constructorul serviciului.
     *
     * @param postRepository repository-ul postarilor
     * @param moderationService serviciul de moderare a textului
     * @param childAccountService serviciul conturilor de copii
     * @param accountService serviciul conturilor de parinti
     * @param notificationService serviciul de notificari
     */
    public PostService(PostRepository postRepository,
                       ModerationService moderationService,
                       ChildAccountService childAccountService,
                       AccountService accountService,
                       NotificationService notificationService) {
        this.postRepository = postRepository;
        this.moderationService = moderationService;
        this.childAccountService = childAccountService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    /**
     * Proceseaza o postare text trimisa de un copil.
     * Analizeaza automat continutul, salveaza postarea cu statusul
     * corespunzator si notifica parintele daca textul este blocat.
     *
     * @param childId identificatorul copilului care posteaza
     * @param content continutul textului postat
     * @return rezultatul postarii cu postarea salvata sau mesajul de eroare
     */
    public PostResult submitTextPost(int childId, String content) {

        Optional<ChildAccount> childOpt = childAccountService.getById(childId);
        if (childOpt.isEmpty()) {
            return new PostResult(false, null, "Contul copilului nu exista.");
        }

        ModerationService.ModerationResult modResult = moderationService.analyzeText(content);
        String status = modResult.blocked() ? "BLOCKED" : "POSTED";

        int id = postRepository.generateId();
        Post post = new Post(id, childId, content, "TEXT", status);
        postRepository.save(post);

        if (modResult.blocked()) {
            notifyParent(childOpt.get(), content);
        }

        return new PostResult(true, post, null);
    }

    /**
     * Returneaza toate postarile unui copil.
     *
     * @param childId identificatorul copilului
     * @return lista cu toate postarile copilului
     */
    public List<Post> getByChildId(int childId) {
        return postRepository.findByChildId(childId);
    }

    /**
     * Returneaza toate postarile din sistem.
     *
     * @return lista cu toate postarile
     */
    public List<Post> getAll() {
        return postRepository.findAll();
    }

    /**
     * Notifica parintele unui copil cand o postare este blocata.
     * Daca parintele nu este gasit, metoda returneaza fara eroare.
     *
     * @param child contul copilului care a postat continut blocat
     * @param blockedText textul care a fost blocat
     */
    private void notifyParent(ChildAccount child, String blockedText) {
        Optional<ParentAccount> parentOpt = accountService.getById(child.getParentId());
        if (parentOpt.isEmpty()) return;

        ParentAccount parent = parentOpt.get();
        notificationService.notifyParentTextBlocked(
                parent.getEmail(), child.getUsername(), blockedText);
    }

    /**
     * Reprezinta rezultatul procesarii unei postari.
     *
     * @param success true daca postarea a fost procesata cu succes, false altfel
     * @param post postarea salvata sau null daca procesarea a esuat
     * @param errorMessage mesajul de eroare sau null daca a reusit
     */
    public record PostResult(boolean success, Post post, String errorMessage) {}
}