package org.example.webapi.mappers;

import org.example.data.model.Comment;
import org.example.data.model.Post;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.exceptionhandler.exception.NotFoundException;
import org.example.webapi.contract.CommentDTO;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CommentMapper {
    private final DataCatalog db;

    public CommentMapper(DataCatalog db) {
        this.db = db;
    }

    public CommentDTO toDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setId(comment.getId());
        commentDTO.setPostId(comment.getPost().getId());
        commentDTO.setAuthorId(comment.getUser().getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setCreatedAt(comment.getCreatedAt());

        return commentDTO;
    }

    public Comment toEntity(CommentDTO commentDTO) {
        Comment comment = new Comment();

        Optional<User> user = db.getUsers().findById(commentDTO.getAuthorId());

        if (user.isPresent()) {
            comment.setUser(user.get());
        } else {
            throw new NotFoundException("User not found with ID: " + commentDTO.getAuthorId());
        }

        Optional<Post> post = db.getPosts().findById(commentDTO.getPostId());

        if (post.isPresent()) {
            comment.setPost(post.get());
        } else {
            throw new NotFoundException("Post not found with ID: " + commentDTO.getPostId());
        }

        comment.setContent(commentDTO.getContent());
        comment.setCreatedAt(commentDTO.getCreatedAt());

        return comment;
    }
}
