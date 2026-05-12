package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.service.ChildAccountService;
import com.student.tuiasi.moderation.service.ChildAccountService.ChildRegistrationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller pentru gestionarea conturilor de copii.
 * Expune endpoint-uri pentru operatii CRUD complete:
 * inregistrare, citire, actualizare si stergere de conturi de copii.
 */
@RestController
@RequestMapping("/api/children")
public class ChildAccountController {

    /** Serviciu pentru gestionarea conturilor de copii. */
    private final ChildAccountService childAccountService;

    /**
     * Constructorul controller-ului.
     *
     * @param childAccountService serviciul conturilor de copii
     */
    public ChildAccountController(ChildAccountService childAccountService) {
        this.childAccountService = childAccountService;
    }

    /**
     * Inregistreaza un cont nou de copil.
     * Returneaza 400 Bad Request daca validarea esueaza.
     * Returneaza 201 Created daca inregistrarea a reusit.
     *
     * @param request datele contului de inregistrat
     * @return contul creat sau mesajul de eroare
     */
    @PostMapping("/register")
    public ResponseEntity<ChildResponse> registerChild(@RequestBody ChildRequest request) {
        ChildRegistrationResult result = childAccountService.registerChild(
                request.username(), request.age(), request.parentId());

        if (!result.success()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ChildResponse(false, result.errorMessage(), null));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ChildResponse(true, "Cont copil creat cu succes!", result.account()));
    }

    /**
     * Returneaza un cont de copil dupa id.
     * Returneaza 404 Not Found daca nu exista.
     *
     * @param id identificatorul contului cautat
     * @return contul gasit sau mesajul de eroare
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChildResponse> getById(@PathVariable int id) {
        Optional<ChildAccount> account = childAccountService.getById(id);
        if (account.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ChildResponse(false, "Contul nu a fost gasit.", null));
        }
        return ResponseEntity.ok(new ChildResponse(true, null, account.get()));
    }

    /**
     * Returneaza toti copiii asociati unui parinte.
     *
     * @param parentId identificatorul parintelui
     * @return lista cu toti copiii parintelui
     */
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<ChildAccount>> getByParentId(@PathVariable int parentId) {
        return ResponseEntity.ok(childAccountService.getByParentId(parentId));
    }

    /**
     * Returneaza toate conturile de copii.
     *
     * @return lista cu toate conturile
     */
    @GetMapping
    public ResponseEntity<List<ChildAccount>> getAll() {
        return ResponseEntity.ok(childAccountService.getAll());
    }

    /**
     * Actualizeaza datele unui cont de copil.
     * Doar campurile furnizate sunt actualizate.
     * Returneaza 404 Not Found daca nu exista.
     *
     * @param id identificatorul contului de actualizat
     * @param request datele de actualizat
     * @return mesajul de confirmare sau eroare
     */
    @PutMapping("/{id}")
    public ResponseEntity<ChildResponse> updateChild(@PathVariable int id,
                                                      @RequestBody UpdateChildRequest request) {
        boolean updated = childAccountService.updateChild(id, request.username(), request.age());
        if (!updated) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ChildResponse(false, "Contul nu a fost gasit.", null));
        }
        return ResponseEntity.ok(new ChildResponse(true, "Cont actualizat cu succes!", null));
    }

    /**
     * Sterge un cont de copil dupa id.
     * Returneaza 404 Not Found daca nu exista.
     *
     * @param id identificatorul contului de sters
     * @return mesajul de confirmare sau eroare
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ChildResponse> deleteById(@PathVariable int id) {
        boolean deleted = childAccountService.deleteById(id);
        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ChildResponse(false, "Contul nu a fost gasit.", null));
        }
        return ResponseEntity.ok(new ChildResponse(true, "Cont sters cu succes!", null));
    }

    /**
     * Reprezinta requestul de inregistrare a unui cont de copil.
     *
     * @param username numele de utilizator dorit
     * @param age varsta copilului
     * @param parentId identificatorul parintelui asociat
     */
    public record ChildRequest(String username, int age, int parentId) {}

    /**
     * Reprezinta requestul de actualizare a unui cont de copil.
     * Campurile null nu sunt actualizate.
     *
     * @param username noul username sau null
     * @param age noua varsta sau null
     */
    public record UpdateChildRequest(String username, Integer age) {}

    /**
     * Reprezinta raspunsul operatiilor pe conturile de copii.
     *
     * @param success true daca operatia a reusit, false altfel
     * @param message mesajul de confirmare sau eroare
     * @param account contul afectat sau null
     */
    public record ChildResponse(boolean success, String message, ChildAccount account) {}
}