package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Optional;

/**
 * Serviciu pentru gestionarea conturilor de parinti.
 * Implementeaza logica de validare la inregistrare:
 * unicitatea username-ului, varsta minima de 18 ani
 * si analiza textului motivatiei prin ModerationService.
 */
@Service
public class AccountService {

    /** Repository pentru accesul la datele conturilor de parinti. */
    private final AccountRepository accountRepository;

    /** Serviciu pentru analiza textului motivatiei la inregistrare. */
    private final ModerationService moderationService;

    /**
     * Constructorul serviciului.
     *
     * @param accountRepository repository-ul conturilor de parinti
     * @param moderationService serviciul de moderare a textului
     */
    public AccountService(AccountRepository accountRepository,
                          ModerationService moderationService) {
        this.accountRepository = accountRepository;
        this.moderationService = moderationService;
    }

    /** Varsta minima necesara pentru crearea unui cont de parinte. */
    private static final int MINIMUM_AGE = 18;

    /**
     * Inregistreaza un cont nou de parinte dupa validarea datelor.
     * Verificarile se fac in ordine: unicitatea username-ului,
     * varsta minima si analiza textului motivatiei.
     *
     * @param username numele de utilizator dorit
     * @param email adresa de email pentru notificari
     * @param birthYear anul nasterii pentru verificarea varstei
     * @param reason motivul inregistrarii, analizat pentru toxicitate
     * @return rezultatul inregistrarii cu contul creat sau mesajul de eroare
     */
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

    /**
     * Returneaza un cont de parinte dupa id.
     *
     * @param id identificatorul contului cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ParentAccount> getById(int id) {
        return accountRepository.findById(id);
    }

    /**
     * Returneaza toate conturile de parinti.
     *
     * @return lista cu toate conturile
     */
    public List<ParentAccount> getAll() {
        return accountRepository.findAll();
    }

    /**
     * Sterge un cont de parinte dupa id.
     *
     * @param id identificatorul contului de sters
     * @return true daca stergerea a reusit, false daca nu a fost gasit
     */
    public boolean deleteById(int id) {
        if (accountRepository.findById(id).isEmpty()) {
            return false;
        }
        accountRepository.deleteById(id);
        return true;
    }

    /**
     * Reprezinta rezultatul inregistrarii unui cont de parinte.
     *
     * @param success true daca inregistrarea a reusit, false altfel
     * @param account contul creat sau null daca inregistrarea a esuat
     * @param errorMessage mesajul de eroare sau null daca a reusit
     */
    public record RegistrationResult(boolean success, ParentAccount account, String errorMessage) {}
}