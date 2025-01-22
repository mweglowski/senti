package org.example.webapi.service;

import org.example.data.model.Comment;
import org.example.data.model.Post;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.exceptionhandler.exception.NotFoundException;
import org.example.webapi.contract.CommentDTO;
import org.example.webapi.mappers.CommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final DataCatalog db;
    private final CommentMapper commentMapper;
    private final LogService logService;

    public CommentService(DataCatalog db, CommentMapper commentMapper, LogService logService) {
        this.commentMapper = commentMapper;
        this.db = db;
        this.logService = logService;
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        Post post = db.getPosts().findById(commentDTO.getPostId())
                .orElseThrow(() -> {
                    logService.log("ERROR", "Post not found while creating a comment in createComment()", "CommentService");
                    return new NotFoundException("Post not found with ID: " + commentDTO.getPostId());
                });

        User user = db.getUsers().findById(commentDTO.getAuthorId())
                .orElseThrow(() -> {
                    logService.log("ERROR", "User not found while creating a comment in createComment()", "CommentService");
                    return new NotFoundException("User not found with ID: " + commentDTO.getAuthorId());
                });

        Comment comment = commentMapper.toEntity(commentDTO);
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = db.getComments().save(comment);

        logService.log("INFO", "Comment has been created successfully in createComment()", "CommentService");

        return commentMapper.toDTO(savedComment);
    }

    public List<CommentDTO> getComments() {
        List<Comment> comments = db.getComments().findAll();

        logService.log("INFO", "Comments have been fetched successfully in getComments()", "CommentService");

        return comments
                .stream()
                .map(commentMapper::toDTO)
                .toList();
    }

    public List<CommentDTO> getCommentsByPostId(Long id) {
        List<Comment> comments = db.getComments().findAllByPostId(id);

        logService.log("INFO", "Comments have been fetched successfully in getComments()", "CommentService");

        return comments
                .stream()
                .map(commentMapper::toDTO)
                .toList();
    }

    public CommentDTO getComment(Long id) {
        Optional<Comment> comment = db.getComments().findById(id);

        CommentDTO commentDTO = comment.map(commentMapper::toDTO).orElseThrow(() -> {
            logService.log("ERROR", "Comment not found in getComment()", "CommentService");
            return new NotFoundException("Comment not found with ID: " + id);
        });

        logService.log("INFO", "Comment has been fetched successfully in getComment()", "CommentService");

        return commentDTO;
    }

    public String deleteComment(Long id) {
        Optional<Comment> comment = db.getComments().findById(id);

        if (comment.isPresent()) {
            db.getComments().deleteById(id);
            logService.log("INFO", "Comment has been deleted successfully in deleteComment()", "CommentService");
            return "Comment has been deleted successfully.";
        }
        logService.log("ERROR", "Comment not found in deleteComment()", "CommentService");
        throw new NotFoundException("Comment not found with ID: " + id);
    }
}
