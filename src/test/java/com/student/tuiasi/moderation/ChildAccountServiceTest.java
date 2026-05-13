package com.student.tuiasi.moderation;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import com.student.tuiasi.moderation.repository.ChildAccountRepository;
import com.student.tuiasi.moderation.service.ChildAccountService;
import com.student.tuiasi.moderation.service.ChildAccountService.ChildRegistrationResult;
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
 * Teste unitare pentru ChildAccountService.
 * Verifica logica de validare la inregistrarea conturilor de copii
 * si operatiile CRUD.
 * Foloseste Mockito pentru a simula dependentele externe.
 */
@ExtendWith(MockitoExtension.class)
class ChildAccountServiceTest {

    /** Mock pentru repository-ul conturilor de copii. */
    @Mock
    private ChildAccountRepository childAccountRepository;

    /** Mock pentru repository-ul conturilor de parinti. */
    @Mock
    private AccountRepository parentAccountRepository;

    /** Instanta reala a serviciului de testat cu mock-urile injectate. */
    @InjectMocks
    private ChildAccountService childAccountService;

    /**
     * Verifica ca inregistrarea reuseste cand toate datele sunt valide.
     * Parintele exista, username unic si varsta intre 5 si 17 ani.
     */
    @Test
    void registerChild_dateValide_returneazaSuccess() {
        // Arrange
        ParentAccount parent = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(parentAccountRepository.findById(1))
                .thenReturn(Optional.of(parent));
        when(childAccountRepository.findByUsername("ion_junior"))
                .thenReturn(Optional.empty());
        when(childAccountRepository.generateId()).thenReturn(1);

        // Act
        ChildRegistrationResult result = childAccountService.registerChild(
                "ion_junior", 10, 1);

        // Assert
        assertTrue(result.success());
        assertNotNull(result.account());
        assertNull(result.errorMessage());
    }

    /**
     * Verifica ca inregistrarea esueaza cand parintele nu exista.
     */
    @Test
    void registerChild_parinteInexistent_returneazaEroare() {
        // Arrange
        when(parentAccountRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act
        ChildRegistrationResult result = childAccountService.registerChild(
                "ion_junior", 10, 999);

        // Assert
        assertFalse(result.success());
        assertEquals("Parintele cu id-ul 999 nu exista.", result.errorMessage());
        assertNull(result.account());
    }

    /**
     * Verifica ca inregistrarea esueaza cand username-ul este deja folosit.
     */
    @Test
    void registerChild_usernameDuplicat_returneazaEroare() {
        // Arrange
        ParentAccount parent = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(parentAccountRepository.findById(1))
                .thenReturn(Optional.of(parent));
        ChildAccount existing = new ChildAccount(1, "ion_junior", 10, 1);
        when(childAccountRepository.findByUsername("ion_junior"))
                .thenReturn(Optional.of(existing));

        // Act
        ChildRegistrationResult result = childAccountService.registerChild(
                "ion_junior", 10, 1);

        // Assert
        assertFalse(result.success());
        assertEquals("Username-ul este deja folosit.", result.errorMessage());
    }

    /**
     * Verifica ca inregistrarea esueaza cand varsta depaseste maximul de 17 ani.
     */
    @Test
    void registerChild_varstaPesteMaxim_returneazaEroare() {
        // Arrange
        ParentAccount parent = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(parentAccountRepository.findById(1))
                .thenReturn(Optional.of(parent));
        when(childAccountRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        // Act
        ChildRegistrationResult result = childAccountService.registerChild(
                "adult_fals", 20, 1);

        // Assert
        assertFalse(result.success());
        assertEquals("Contul de copil poate fi creat doar pentru persoane sub 18 ani.",
                result.errorMessage());
    }

    /**
     * Verifica ca inregistrarea esueaza cand varsta este sub minimul de 5 ani.
     */
    @Test
    void registerChild_varstaSubMinim_returneazaEroare() {
        // Arrange
        ParentAccount parent = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(parentAccountRepository.findById(1))
                .thenReturn(Optional.of(parent));
        when(childAccountRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        // Act
        ChildRegistrationResult result = childAccountService.registerChild(
                "bebelus", 2, 1);

        // Assert
        assertFalse(result.success());
        assertEquals("Varsta introdusa nu este valida.", result.errorMessage());
    }

    /**
     * Verifica ca actualizarea reuseste cand contul exista.
     * Verifica ca doar campurile nenule sunt actualizate.
     */
    @Test
    void updateChild_contExistent_returneazaTrue() {
        // Arrange
        ChildAccount child = new ChildAccount(1, "ion_junior", 10, 1);
        when(childAccountRepository.findById(1))
                .thenReturn(Optional.of(child));

        // Act
        boolean result = childAccountService.updateChild(1, "ion_nou", 11);

        // Assert
        assertTrue(result);
        assertEquals("ion_nou", child.getUsername());
        assertEquals(11, child.getAge());
    }

    /**
     * Verifica ca actualizarea esueaza cand contul nu exista.
     */
    @Test
    void updateChild_contInexistent_returneazaFalse() {
        // Arrange
        when(childAccountRepository.findById(999))
                .thenReturn(Optional.empty());

        // Act
        boolean result = childAccountService.updateChild(999, "ion_nou", 11);

        // Assert
        assertFalse(result);
    }

    /**
     * Verifica ca stergerea reuseste cand contul exista.
     */
    @Test
    void deleteById_contExistent_returneazaTrue() {
        // Arrange
        when(childAccountRepository.deleteById(1)).thenReturn(true);

        // Act
        boolean result = childAccountService.deleteById(1);

        // Assert
        assertTrue(result);
    }

    /**
     * Verifica ca stergerea esueaza cand contul nu exista.
     */
    @Test
    void deleteById_contInexistent_returneazaFalse() {
        // Arrange
        when(childAccountRepository.deleteById(999)).thenReturn(false);

        // Act
        boolean result = childAccountService.deleteById(999);

        // Assert
        assertFalse(result);
    }
}