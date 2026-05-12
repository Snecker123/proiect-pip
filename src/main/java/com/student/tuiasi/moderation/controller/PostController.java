package com.student.tuiasi.moderation.controller;

import com.student.tuiasi.moderation.model.Post;
import com.student.tuiasi.moderation.service.PostService;
import com.student.tuiasi.moderation.service.PostService.PostResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/text")
    public ResponseEntity<PostResponse> submitTextPost(@RequestBody TextPostRequest request) {
        PostResult result = postService.submitTextPost(request.childId(), request.content());

        if (!result.success()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new PostResponse(false, result.errorMessage(), null));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new PostResponse(true, null, result.post()));
    }

    @GetMapping("/child/{childId}")
    public ResponseEntity<List<Post>> getByChildId(@PathVariable int childId) {
        return ResponseEntity.ok(postService.getByChildId(childId));
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    public record TextPostRequest(int childId, String content) {}
    public record PostResponse(boolean success, String message, Post post) {}
}