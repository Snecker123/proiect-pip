package com.student.tuiasi.moderation;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.model.Post;
import com.student.tuiasi.moderation.repository.PostRepository;
import com.student.tuiasi.moderation.service.AccountService;
import com.student.tuiasi.moderation.service.ChildAccountService;
import com.student.tuiasi.moderation.service.ModerationService;
import com.student.tuiasi.moderation.service.ModerationService.ModerationResult;
import com.student.tuiasi.moderation.service.NotificationService;
import com.student.tuiasi.moderation.service.PostService;
import com.student.tuiasi.moderation.service.PostService.PostResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Teste unitare pentru PostService.
 * Verifica fluxul complet de postare — moderare automata,
 * salvarea postarii si notificarea parintelui.
 * Foloseste Mockito pentru a simula dependentele externe.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    /** Mock pentru repository-ul postarilor. */
    @Mock
    private PostRepository postRepository;

    /** Mock pentru serviciul de moderare a textului. */
    @Mock
    private ModerationService moderationService;

    /** Mock pentru serviciul conturilor de copii. */
    @Mock
    private ChildAccountService childAccountService;

    /** Mock pentru serviciul conturilor de parinti. */
    @Mock
    private AccountService accountService;

    /** Mock pentru serviciul de notificari. */
    @Mock
    private NotificationService notificationService;

    /** Instanta reala a serviciului de testat cu mock-urile injectate. */
    @InjectMocks
    private PostService postService;

    /**
     * Verifica ca o postare cu text normal este salvata cu statusul POSTED.
     * Verifica si ca parintele nu este notificat.
     */
    @Test
    void submitTextPost_textNormal_returneazaPOSTED() {
        // Arrange
        ChildAccount child = new ChildAccount(1, "ion_junior", 10, 1);
        when(childAccountService.getById(1))
                .thenReturn(Optional.of(child));
        when(moderationService.analyzeText(anyString()))
                .thenReturn(new ModerationResult(false, 0.001, "toxicity"));
        when(postRepository.generateId()).thenReturn(1);

        // Act
        PostResult result = postService.submitTextPost(1, "Buna ziua!");

        // Assert
        assertTrue(result.success());
        assertNotNull(result.post());
        assertEquals("POSTED", result.post().getStatus());
        assertNull(result.errorMessage());
        verify(notificationService, never())
                .notifyParentTextBlocked(anyString(), anyString(), anyString());
    }

    /**
     * Verifica ca o postare cu text toxic este salvata cu statusul BLOCKED.
     * Verifica si ca parintele este notificat prin email.
     */
    @Test
    void submitTextPost_textToxic_returneazaBLOCKED() {
        // Arrange
        ChildAccount child = new ChildAccount(1, "ion_junior", 10, 1);
        ParentAccount parent = new ParentAccount(1, "tata_ion",
                "tata@gmail.com", 1985, "motiv");
        when(childAccountService.getById(1))
                .thenReturn(Optional.of(child));
        when(moderationService.analyzeText(anyString()))
                .thenReturn(new ModerationResult(true, 0.93, "toxicity"));
        when(accountService.getById(1))
                .thenReturn(Optional.of(parent));
        when(postRepository.generateId()).thenReturn(1);

        // Act
        PostResult result = postService.submitTextPost(1, "I hate you");

        // Assert
        assertTrue(result.success());
        assertNotNull(result.post());
        assertEquals("BLOCKED", result.post().getStatus());
        verify(notificationService).notifyParentTextBlocked(
                "tata@gmail.com", "ion_junior", "I hate you");
    }

    /**
     * Verifica ca postarea esueaza cand copilul nu exista.
     * Verifica si ca postarea nu este salvata.
     */
    @Test
    void submitTextPost_copilInexistent_returneazaEroare() {
        // Arrange
        when(childAccountService.getById(999))
                .thenReturn(Optional.empty());

        // Act
        PostResult result = postService.submitTextPost(999, "orice text");

        // Assert
        assertFalse(result.success());
        assertEquals("Contul copilului nu exista.", result.errorMessage());
        assertNull(result.post());
        verify(postRepository, never()).save(any());
    }

    /**
     * Verifica ca postarea e salvata chiar daca parintele nu e gasit.
     * Sistemul nu crapa daca parintele lipseste din sistem.
     */
    @Test
    void submitTextPost_textToxic_parinteInexistent_postareaESalvata() {
        // Arrange
        ChildAccount child = new ChildAccount(1, "ion_junior", 10, 1);
        when(childAccountService.getById(1))
                .thenReturn(Optional.of(child));
        when(moderationService.analyzeText(anyString()))
                .thenReturn(new ModerationResult(true, 0.93, "toxicity"));
        when(accountService.getById(1))
                .thenReturn(Optional.empty());
        when(postRepository.generateId()).thenReturn(1);

        // Act
        PostResult result = postService.submitTextPost(1, "I hate you");

        // Assert
        assertTrue(result.success());
        assertEquals("BLOCKED", result.post().getStatus());
        verify(notificationService, never())
                .notifyParentTextBlocked(anyString(), anyString(), anyString());
    }

    /**
     * Verifica ca getByChildId returneaza toate postarile unui copil.
     */
    @Test
    void getByChildId_returneazaPostarileCopiluluI() {
        // Arrange
        List<Post> posts = List.of(
                new Post(1, 1, "Buna ziua!", "TEXT", "POSTED"),
                new Post(2, 1, "I hate you", "TEXT", "BLOCKED")
        );
        when(postRepository.findByChildId(1)).thenReturn(posts);

        // Act
        List<Post> result = postService.getByChildId(1);

        // Assert
        assertEquals(2, result.size());
        assertEquals("POSTED", result.get(0).getStatus());
        assertEquals("BLOCKED", result.get(1).getStatus());
    }
}