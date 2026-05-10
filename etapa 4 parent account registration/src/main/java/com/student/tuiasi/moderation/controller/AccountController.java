package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.service.AccountService;
import com.student.tuiasi.moderation.service.AccountService.RegistrationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

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

    @GetMapping
    public ResponseEntity<List<ParentAccount>> getAll() {
        return ResponseEntity.ok(accountService.getAll());
    }

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

    public record RegisterRequest(String username, String email,
                                   int birthYear, String reason) {}

    public record AccountResponse(boolean success, String message, ParentAccount account) {}
}