package com.student.tuiasi.moderation.service;

import com.student.tuiasi.moderation.model.ChildAccount;
import com.student.tuiasi.moderation.model.ParentAccount;
import com.student.tuiasi.moderation.model.Post;
import com.student.tuiasi.moderation.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final ModerationService moderationService;
    private final ChildAccountService childAccountService;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public PostService(PostRepository postRepository,
                       ModerationService moderationService,
                       ChildAccountService childAccountService,
                       AccountService accountService,
                       NotificationService notificationService) {
        this.postRepository = postRepository;
        this.moderationService = moderationService;
        this.childAccountService = childAccountService;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    public PostResult submitTextPost(int childId, String content) {

        Optional<ChildAccount> childOpt = childAccountService.getById(childId);
        if (childOpt.isEmpty()) {
            return new PostResult(false, null, "Contul copilului nu exista.");
        }

        ModerationService.ModerationResult modResult = moderationService.analyzeText(content);
        String status = modResult.blocked() ? "BLOCKED" : "POSTED";

        int id = postRepository.generateId();
        Post post = new Post(id, childId, content, "TEXT", status);
        postRepository.save(post);

        if (modResult.blocked()) {
            notifyParent(childOpt.get(), content);
        }

        return new PostResult(true, post, null);
    }

    public List<Post> getByChildId(int childId) {
        return postRepository.findByChildId(childId);
    }

    public List<Post> getAll() {
        return postRepository.findAll();
    }

    private void notifyParent(ChildAccount child, String blockedText) {
        Optional<ParentAccount> parentOpt = accountService.getById(child.getParentId());
        if (parentOpt.isEmpty()) return;

        ParentAccount parent = parentOpt.get();
        notificationService.notifyParentTextBlocked(
                parent.getEmail(), child.getUsername(), blockedText);
    }

    public record PostResult(boolean success, Post post, String errorMessage) {}
}