package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.Post;
import com.student.tuiasi.moderation.service.PostService;
import com.student.tuiasi.moderation.service.PostService.PostResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pentru gestionarea postarilor copiilor.
 * Expune endpoint-uri pentru trimiterea si citirea postarilor.
 * Moderarea continutului se face automat in fundal prin PostService —
 * utilizatorul nu stie ca exista un sistem de moderare.
 */
@RestController
@RequestMapping("/api/posts")
public class PostController {

    /** Serviciu pentru gestionarea postarilor. */
    private final PostService postService;

    /**
     * Constructorul controller-ului.
     *
     * @param postService serviciul postarilor
     */
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * Proceseaza o postare text trimisa de un copil.
     * Continutul este moderat automat in fundal.
     * Returneaza 400 Bad Request daca copilul nu exista.
     * Returneaza 201 Created daca postarea a fost procesata.
     *
     * @param request requestul cu childId si continutul textului
     * @return postarea salvata cu statusul ei sau mesajul de eroare
     */
    @PostMapping("/text")
    public ResponseEntity<PostResponse> submitTextPost(@RequestBody TextPostRequest request) {
        PostResult result = postService.submitTextPost(request.childId(), request.content());

        if (!result.success()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new PostResponse(false, result.errorMessage(), null));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new PostResponse(true, null, result.post()));
    }

    /**
     * Returneaza toate postarile unui copil.
     *
     * @param childId identificatorul copilului
     * @return lista cu toate postarile copilului
     */
    @GetMapping("/child/{childId}")
    public ResponseEntity<List<Post>> getByChildId(@PathVariable int childId) {
        return ResponseEntity.ok(postService.getByChildId(childId));
    }

    /**
     * Returneaza toate postarile din sistem.
     *
     * @return lista cu toate postarile
     */
    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    /**
     * Reprezinta requestul de trimitere a unei postari text.
     *
     * @param childId identificatorul copilului care posteaza
     * @param content continutul textului postat
     */
    public record TextPostRequest(int childId, String content) {}

    /**
     * Reprezinta raspunsul procesarii unei postari.
     *
     * @param success true daca postarea a fost procesata cu succes, false altfel
     * @param message mesajul de eroare sau null daca a reusit
     * @param post postarea salvata sau null daca procesarea a esuat
     */
    public record PostResponse(boolean success, String message, Post post) {}
}