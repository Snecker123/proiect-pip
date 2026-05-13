package com.student.tuiasi.moderation;

import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitare pentru AccountRepository.
 * Verifica operatiile de stocare, cautare si stergere
 * a conturilor de parinti in lista din memorie.
 * Nu foloseste Mockito — testeaza direct implementarea.
 */
class AccountRepositoryTest {

    /** Instanta reala a repository-ului de testat. */
    private AccountRepository accountRepository;

    /**
     * Creeaza o instanta noua a repository-ului inainte de fiecare test.
     * Asigura ca fiecare test porneste cu o lista goala.
     */
    @BeforeEach
    void setUp() {
        accountRepository = new AccountRepository();
    }

    /**
     * Verifica ca un cont salvat poate fi gasit dupa id.
     */
    @Test
    void save_siFindById_returneazaContulSalvat() {
        // Arrange
        int id = accountRepository.generateId();
        ParentAccount account = new ParentAccount(id, "tata_ion",
                "tata@gmail.com", 1985, "motiv");

        // Act
        accountRepository.save(account);
        Optional<ParentAccount> result = accountRepository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("tata_ion", result.get().getUsername());
        assertEquals("tata@gmail.com", result.get().getEmail());
    }

    /**
     * Verifica ca findById returneaza Optional gol pentru un id inexistent.
     */
    @Test
    void findById_idInexistent_returneazaOptionalGol() {
        // Act
        Optional<ParentAccount> result = accountRepository.findById(999);

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Verifica ca un cont salvat poate fi gasit dupa username.
     */
    @Test
    void findByUsername_usernameExistent_returneazaContul() {
        // Arrange
        int id = accountRepository.generateId();
        ParentAccount account = new ParentAccount(id, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        accountRepository.save(account);

        // Act
        Optional<ParentAccount> result = accountRepository.findByUsername("tata_ion");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    /**
     * Verifica ca findByUsername returneaza Optional gol pentru un username inexistent.
     */
    @Test
    void findByUsername_usernameInexistent_returneazaOptionalGol() {
        // Act
        Optional<ParentAccount> result = accountRepository.findByUsername("inexistent");

        // Assert
        assertTrue(result.isEmpty());
    }

    /**
     * Verifica ca findAll returneaza toate conturile salvate.
     */
    @Test
    void findAll_returneazaToateConturile() {
        // Arrange
        accountRepository.save(new ParentAccount(
                accountRepository.generateId(), "tata_ion", "tata@gmail.com", 1985, "motiv"));
        accountRepository.save(new ParentAccount(
                accountRepository.generateId(), "mama_maria", "mama@gmail.com", 1987, "motiv"));

        // Act
        List<ParentAccount> result = accountRepository.findAll();

        // Assert
        assertEquals(2, result.size());
    }

    /**
     * Verifica ca findAll returneaza o copie a listei.
     * Modificarea listei returnate nu afecteaza lista interna.
     */
    @Test
    void findAll_returneazaCopie_nuListaOriginala() {
        // Arrange
        accountRepository.save(new ParentAccount(
                accountRepository.generateId(), "tata_ion", "tata@gmail.com", 1985, "motiv"));

        // Act
        List<ParentAccount> result = accountRepository.findAll();
        result.clear();

        // Assert
        assertEquals(1, accountRepository.findAll().size());
    }

    /**
     * Verifica ca un cont sters nu mai poate fi gasit.
     */
    @Test
    void deleteById_contExistent_esteSterse() {
        // Arrange
        int id = accountRepository.generateId();
        accountRepository.save(new ParentAccount(id, "tata_ion",
                "tata@gmail.com", 1985, "motiv"));

        // Act
        accountRepository.deleteById(id);

        // Assert
        assertTrue(accountRepository.findById(id).isEmpty());
    }

    /**
     * Verifica ca stergerea unui id inexistent nu arunca exceptie.
     */
    @Test
    void deleteById_idInexistent_nuAruncaExceptie() {
        // Act & Assert
        assertDoesNotThrow(() -> accountRepository.deleteById(999));
    }

    /**
     * Verifica ca generateId returneaza id-uri unice si crescatoare.
     */
    @Test
    void generateId_returneazaIdUnice() {
        // Act
        int id1 = accountRepository.generateId();
        int id2 = accountRepository.generateId();
        int id3 = accountRepository.generateId();

        // Assert
        assertEquals(1, id1);
        assertEquals(2, id2);
        assertEquals(3, id3);
    }
}