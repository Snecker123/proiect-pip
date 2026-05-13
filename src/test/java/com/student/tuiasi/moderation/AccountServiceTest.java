package com.student.tuiasi.moderation;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import com.student.tuiasi.moderation.service.AccountService;
import com.student.tuiasi.moderation.service.AccountService.RegistrationResult;
import com.student.tuiasi.moderation.service.ModerationService;
import com.student.tuiasi.moderation.service.ModerationService.ModerationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru AccountService.
 * Verifica logica de validare la inregistrarea conturilor de parinti
 * si operatiile de stergere.
 * Foloseste Mockito pentru a simula dependentele externe.
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    /** Mock pentru repository-ul conturilor de parinti. */
    @Mock
    private AccountRepository accountRepository;

    /** Mock pentru serviciul de moderare a textului. */
    @Mock
    private ModerationService moderationService;

    /** Instanta reala a serviciului de testat cu mock-urile injectate. */
    @InjectMocks
    private AccountService accountService;

    /**
     * Verifica ca inregistrarea reuseste cand toate datele sunt valide.
     * Username unic, varsta peste 18 ani si text valid.
     */
    @Test
    void registerParent_dateValide_returneazaSuccess() {
        // Arrange
        when(accountRepository.findByUsername("tata_ion"))
                .thenReturn(Optional.empty());
        when(moderationService.analyzeText(anyString()))
                .thenReturn(new ModerationResult(false, 0.001, "toxicity"));
        when(accountRepository.generateId()).thenReturn(1);

        // Act
        RegistrationResult result = accountService.registerParent(
                "tata_ion", "tata@gmail.com", 1985,
                "Vreau sa monitorizez activitatea copilului meu");

        // Assert
        assertTrue(result.success());
        assertNotNull(result.account());
        assertNull(result.errorMessage());
    }

    /**
     * Verifica ca inregistrarea esueaza cand username-ul este deja folosit.
     */
    @Test
    void registerParent_usernameDuplicat_returneazaEroare() {
        // Arrange
        ParentAccount existing = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(accountRepository.findByUsername("tata_ion"))
                .thenReturn(Optional.of(existing));

        // Act
        RegistrationResult result = accountService.registerParent(
                "tata_ion", "alt@gmail.com", 1985, "motiv");

        // Assert
        assertFalse(result.success());
        assertEquals("Username-ul este deja folosit.", result.errorMessage());
        assertNull(result.account());
    }

    /**
     * Verifica ca inregistrarea esueaza cand varsta este sub minimul de 18 ani.
     * Simuleaza un copil care incearca sa isi faca cont de parinte.
     */
    @Test
    void registerParent_varstaSubMinim_returneazaEroare() {
        // Arrange
        when(accountRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        // Act
        RegistrationResult result = accountService.registerParent(
                "copil_rau", "copil@gmail.com", 2010, "motiv");

        // Assert
        assertFalse(result.success());
        assertEquals("Trebuie sa ai cel putin 18 ani pentru a crea un cont de parinte.",
                result.errorMessage());
    }

    /**
     * Verifica ca inregistrarea esueaza cand motivatia contine text toxic.
     */
    @Test
    void registerParent_textToxic_returneazaEroare() {
        // Arrange
        when(accountRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        when(moderationService.analyzeText(anyString()))
                .thenReturn(new ModerationResult(true, 0.93, "toxicity"));

        // Act
        RegistrationResult result = accountService.registerParent(
                "user_toxic", "toxic@gmail.com", 1985,
                "I hate children");

        // Assert
        assertFalse(result.success());
        assertEquals("Textul introdus a fost detectat ca inadecvat. Inregistrare respinsa.",
                result.errorMessage());
    }

    /**
     * Verifica ca stergerea reuseste cand contul exista.
     * Verifica si ca deleteById din repository este apelat exact o data.
     */
    @Test
    void deleteById_contExistent_returneazaTrue() {
        // Arrange
        ParentAccount account = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(accountRepository.findById(1))
                .thenReturn(Optional.of(account));

        // Act
        boolean result = accountService.deleteById(1);

        // Assert
        assertTrue(result);
        verify(accountRepository).deleteById(1);
    }

    /**
     * Verifica ca stergerea esueaza cand contul nu exista.
     * Verifica si ca deleteById din repository nu este apelat.
     */
    @Test
    void deleteById_contInexistent_returneazaFalse() {
        // Arrange
        when(accountRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act
        boolean result = accountService.deleteById(999);

        // Assert
        assertFalse(result);
        verify(accountRepository, never()).deleteById(anyInt());
    }
}