package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.service.ChildAccountService;
import com.student.tuiasi.moderation.service.ChildAccountService.ChildRegistrationResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/children")
public class ChildAccountController {

    private final ChildAccountService childAccountService;

    public ChildAccountController(ChildAccountService childAccountService) {
        this.childAccountService = childAccountService;
    }

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

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<ChildAccount>> getByParentId(@PathVariable int parentId) {
        return ResponseEntity.ok(childAccountService.getByParentId(parentId));
    }

    @GetMapping
    public ResponseEntity<List<ChildAccount>> getAll() {
        return ResponseEntity.ok(childAccountService.getAll());
    }

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

    public record ChildRequest(String username, int age, int parentId) {}
    public record UpdateChildRequest(String username, Integer age) {}
    public record ChildResponse(boolean success, String message, ChildAccount account) {}
}