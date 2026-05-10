package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import com.student.tuiasi.moderation.repository.ChildAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ChildAccountService {

    private final ChildAccountRepository childAccountRepository;
    private final AccountRepository parentAccountRepository;

    public ChildAccountService(ChildAccountRepository childAccountRepository,
                                AccountRepository parentAccountRepository) {
        this.childAccountRepository = childAccountRepository;
        this.parentAccountRepository = parentAccountRepository;
    }

    private static final int MAXIMUM_AGE = 17;

    public ChildRegistrationResult registerChild(String username, int age, int parentId) {

        if (parentAccountRepository.findById(parentId).isEmpty()) {
            return new ChildRegistrationResult(false, null,
                    "Parintele cu id-ul " + parentId + " nu exista.");
        }

        if (childAccountRepository.findByUsername(username).isPresent()) {
            return new ChildRegistrationResult(false, null,
                    "Username-ul este deja folosit.");
        }

        if (age > MAXIMUM_AGE) {
            return new ChildRegistrationResult(false, null,
                    "Contul de copil poate fi creat doar pentru persoane sub 18 ani.");
        }

        if (age < 5) {
            return new ChildRegistrationResult(false, null,
                    "Varsta introdusa nu este valida.");
        }

        int id = childAccountRepository.generateId();
        ChildAccount child = new ChildAccount(id, username, age, parentId);
        childAccountRepository.save(child);

        return new ChildRegistrationResult(true, child, null);
    }

    public Optional<ChildAccount> getById(int id) {
        return childAccountRepository.findById(id);
    }

    public List<ChildAccount> getByParentId(int parentId) {
        return childAccountRepository.findByParentId(parentId);
    }

    public List<ChildAccount> getAll() {
        return childAccountRepository.findAll();
    }

    public boolean deleteById(int id) {
        return childAccountRepository.deleteById(id);
    }

    public boolean updateChild(int id, String newUsername, Integer newAge) {
        Optional<ChildAccount> optional = childAccountRepository.findById(id);
        if (optional.isEmpty()) return false;

        ChildAccount child = optional.get();
        if (newUsername != null) child.setUsername(newUsername);
        if (newAge != null) child.setAge(newAge);
        return true;
    }

    public record ChildRegistrationResult(boolean success, ChildAccount account, String errorMessage) {}
}