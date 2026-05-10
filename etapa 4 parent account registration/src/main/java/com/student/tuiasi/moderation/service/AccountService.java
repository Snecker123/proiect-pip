package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final ModerationService moderationService;

    public AccountService(AccountRepository accountRepository,
                          ModerationService moderationService) {
        this.accountRepository = accountRepository;
        this.moderationService = moderationService;
    }

    private static final int MINIMUM_AGE = 18;

    public RegistrationResult registerParent(String username, String email,
                                              int birthYear, String reason) {

        if (accountRepository.findByUsername(username).isPresent()) {
            return new RegistrationResult(false, null, "Username-ul este deja folosit.");
        }

        int currentYear = Year.now().getValue();
        int age = currentYear - birthYear;
        if (age < MINIMUM_AGE) {
            return new RegistrationResult(false, null,
                "Trebuie sa ai cel putin 18 ani pentru a crea un cont de parinte.");
        }

        ModerationService.ModerationResult textAnalysis = moderationService.analyzeText(reason);
        if (textAnalysis.blocked()) {
            return new RegistrationResult(false, null,
                "Textul introdus a fost detectat ca inadecvat. Inregistrare respinsa.");
        }

        int id = accountRepository.generateId();
        ParentAccount account = new ParentAccount(id, username, email, birthYear, reason);
        account.setApproved(true);
        accountRepository.save(account);

        return new RegistrationResult(true, account, null);
    }

    public Optional<ParentAccount> getById(int id) {
        return accountRepository.findById(id);
    }

    public List<ParentAccount> getAll() {
        return accountRepository.findAll();
    }

    public boolean deleteById(int id) {
        if (accountRepository.findById(id).isEmpty()) {
            return false;
        }
        accountRepository.deleteById(id);
        return true;
    }

    public record RegistrationResult(boolean success, ParentAccount account, String errorMessage) {}
}