package org.example.webapi.controller;

import org.example.webapi.contract.CommentDTO;
import org.example.webapi.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO comment) {
        CommentDTO createdComment = commentService.createComment(comment);

        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getComments(@RequestParam(required = false) Long postId) {
        List<CommentDTO> comments;
        if (postId == null) {
            comments = commentService.getComments();
        } else {
            comments = commentService.getCommentsByPostId(postId);
        }
        return new ResponseEntity<>(comments, HttpStatus.FOUND);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getComment(@PathVariable Long id) {
        CommentDTO commentDTO = commentService.getComment(id);

        return new ResponseEntity<>(commentDTO, HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        String successMessage = commentService.deleteComment(id);

        if (successMessage != null) {
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }
}