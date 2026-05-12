package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.service.AccountService;
import com.student.tuiasi.moderation.service.AccountService.RegistrationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller pentru gestionarea conturilor de parinti.
 * Expune endpoint-uri pentru inregistrare, citire si stergere
 * de conturi de parinti.
 */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    /** Serviciu pentru gestionarea conturilor de parinti. */
    private final AccountService accountService;

    /**
     * Constructorul controller-ului.
     *
     * @param accountService serviciul conturilor de parinti
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Inregistreaza un cont nou de parinte.
     * Returneaza 400 Bad Request daca validarea esueaza.
     * Returneaza 201 Created daca inregistrarea a reusit.
     *
     * @param request datele contului de inregistrat
     * @return contul creat sau mesajul de eroare
     */
    @PostMapping("/register-parent")
    public ResponseEntity<AccountResponse> registerParent(@RequestBody RegisterRequest request) {
        RegistrationResult result = accountService.registerParent(
                request.username(), request.email(),
                request.birthYear(), request.reason());

        if (!result.success()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new AccountResponse(false, result.errorMessage(), null));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AccountResponse(true, "Cont creat cu succes!", result.account()));
    }

    /**
     * Returneaza un cont de parinte dupa id.
     * Returneaza 404 Not Found daca nu exista.
     *
     * @param id identificatorul contului cautat
     * @return contul gasit sau mesajul de eroare
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getById(@PathVariable int id) {
        Optional<ParentAccount> account = accountService.getById(id);
        if (account.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new AccountResponse(false, "Contul nu a fost gasit.", null));
        }
        return ResponseEntity.ok(new AccountResponse(true, null, account.get()));
    }

    /**
     * Returneaza toate conturile de parinti.
     *
     * @return lista cu toate conturile
     */
    @GetMapping
    public ResponseEntity<List<ParentAccount>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

    /**
     * Sterge un cont de parinte dupa id.
     * Returneaza 404 Not Found daca nu exista.
     *
     * @param id identificatorul contului de sters
     * @return mesajul de confirmare sau eroare
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<AccountResponse> deleteById(@PathVariable int id) {
        boolean deleted = accountService.deleteById(id);
        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new AccountResponse(false, "Contul nu a fost gasit.", null));
        }
        return ResponseEntity.ok(new AccountResponse(true, "Cont sters cu succes!", null));
    }

    /**
     * Reprezinta requestul de inregistrare a unui cont de parinte.
     *
     * @param username numele de utilizator dorit
     * @param email adresa de email pentru notificari
     * @param birthYear anul nasterii pentru verificarea varstei
     * @param reason motivul inregistrarii
     */
    public record RegisterRequest(String username, String email,
                                   int birthYear, String reason) {}

    /**
     * Reprezinta raspunsul operatiilor pe conturile de parinti.
     *
     * @param success true daca operatia a reusit, false altfel
     * @param message mesajul de confirmare sau eroare
     * @param account contul afectat sau null
     */
    public record AccountResponse(boolean success, String message, ParentAccount account) {}
}