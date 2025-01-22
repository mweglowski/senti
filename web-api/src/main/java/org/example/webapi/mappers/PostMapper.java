package org.example.webapi.mappers;

import org.example.data.model.Post;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.webapi.contract.PostDTO;
import org.example.exceptionhandler.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PostMapper {
    private final DataCatalog db;

    public PostMapper(DataCatalog db) {
        this.db = db;
    }

    public PostDTO toDTO(Post post) {
        PostDTO postDTO = new PostDTO();

        postDTO.setId(post.getId());
        postDTO.setAuthorId(post.getUser().getId());
        postDTO.setContent(post.getContent());
        postDTO.setCreatedAt(post.getCreatedAt());
        postDTO.setTitle(post.getTitle());

        return postDTO;
    }

    public Post toEntity(PostDTO postDTO) {
        Post post = new Post();

        Optional<User> user = db.getUsers().findById(postDTO.getAuthorId());

        if (user.isPresent()) {
            post.setUser(user.get());
            post.setTitle(postDTO.getTitle());
            post.setContent(postDTO.getContent());
            post.setCreatedAt(postDTO.getCreatedAt());

            return post;
        }
        throw new NotFoundException("User not found with ID: " + postDTO.getAuthorId());
    }
}
