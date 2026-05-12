package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import com.student.tuiasi.moderation.repository.ChildAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Serviciu pentru gestionarea conturilor de copii.
 * Implementeaza logica de validare la inregistrare:
 * existenta parintelui, unicitatea username-ului
 * si incadrarea varstei intre 5 si 17 ani.
 * Suporta operatii CRUD complete pentru conturile de copii.
 */
@Service
public class ChildAccountService {

    /** Repository pentru accesul la datele conturilor de copii. */
    private final ChildAccountRepository childAccountRepository;

    /** Repository pentru verificarea existentei parintelui. */
    private final AccountRepository parentAccountRepository;

    /**
     * Constructorul serviciului.
     *
     * @param childAccountRepository repository-ul conturilor de copii
     * @param parentAccountRepository repository-ul conturilor de parinti
     */
    public ChildAccountService(ChildAccountRepository childAccountRepository,
                                AccountRepository parentAccountRepository) {
        this.childAccountRepository = childAccountRepository;
        this.parentAccountRepository = parentAccountRepository;
    }

    /** Varsta maxima permisa pentru un cont de copil. */
    private static final int MAXIMUM_AGE = 17;

    /**
     * Inregistreaza un cont nou de copil dupa validarea datelor.
     * Verificarile se fac in ordine: existenta parintelui,
     * unicitatea username-ului si incadrarea varstei.
     *
     * @param username numele de utilizator dorit
     * @param age varsta copilului
     * @param parentId identificatorul parintelui asociat
     * @return rezultatul inregistrarii cu contul creat sau mesajul de eroare
     */
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

    /**
     * Returneaza un cont de copil dupa id.
     *
     * @param id identificatorul contului cautat
     * @return Optional cu contul gasit sau Optional gol daca nu exista
     */
    public Optional<ChildAccount> getById(int id) {
        return childAccountRepository.findById(id);
    }

    /**
     * Returneaza toti copiii asociati unui parinte.
     *
     * @param parentId identificatorul parintelui
     * @return lista cu toti copiii parintelui
     */
    public List<ChildAccount> getByParentId(int parentId) {
        return childAccountRepository.findByParentId(parentId);
    }

    /**
     * Returneaza toate conturile de copii.
     *
     * @return lista cu toate conturile
     */
    public List<ChildAccount> getAll() {
        return childAccountRepository.findAll();
    }

    /**
     * Sterge un cont de copil dupa id.
     *
     * @param id identificatorul contului de sters
     * @return true daca stergerea a reusit, false daca nu a fost gasit
     */
    public boolean deleteById(int id) {
        return childAccountRepository.deleteById(id);
    }

    /**
     * Actualizeaza datele unui cont de copil.
     * Doar campurile nenule sunt actualizate.
     *
     * @param id identificatorul contului de actualizat
     * @param newUsername noul username sau null daca nu se schimba
     * @param newAge noua varsta sau null daca nu se schimba
     * @return true daca actualizarea a reusit, false daca nu a fost gasit
     */
    public boolean updateChild(int id, String newUsername, Integer newAge) {
        Optional<ChildAccount> optional = childAccountRepository.findById(id);
        if (optional.isEmpty()) return false;

        ChildAccount child = optional.get();
        if (newUsername != null) child.setUsername(newUsername);
        if (newAge != null) child.setAge(newAge);
        return true;
    }

    /**
     * Reprezinta rezultatul inregistrarii unui cont de copil.
     *
     * @param success true daca inregistrarea a reusit, false altfel
     * @param account contul creat sau null daca inregistrarea a esuat
     * @param errorMessage mesajul de eroare sau null daca a reusit
     */
    public record ChildRegistrationResult(boolean success, ChildAccount account, String errorMessage) {}
}