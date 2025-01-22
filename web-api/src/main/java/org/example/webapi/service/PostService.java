package org.example.webapi.service;

import org.example.data.model.Post;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.webapi.contract.PostDTO;
import org.example.exceptionhandler.exception.NotFoundException;
import org.example.webapi.mappers.PostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
//    private final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final DataCatalog db;
    private final PostMapper postMapper;
    private final LogService logService;

    public PostService(DataCatalog db, PostMapper postMapper, LogService logService) {
        this.db = db;
        this.postMapper = postMapper;
        this.logService = logService;
    }

    public PostDTO createPost(PostDTO postDto) {
        User user = db.getUsers().findById(postDto.getAuthorId())
                .orElseThrow(() -> {
                    logService.log("ERROR", "User not found while creating a post in createPost()", "PostService");
                    return new NotFoundException("User not found with ID: " + postDto.getAuthorId());
                });

        Post post = postMapper.toEntity(postDto);
        post.setUser(user);

        Post savedPost = db.getPosts().save(post);

        logService.log("INFO", "Post has been created successfully in createPost()", "PostService");

        return postMapper.toDTO(savedPost);
    }

    public List<PostDTO> getPosts() {
        List<Post> posts = db.getPosts().findAll();

        logService.log("INFO", "Posts have been fetched successfully in getPosts()", "PostService");

        return posts
                .stream()
                .map(postMapper::toDTO)
                .toList();
    }

    public PostDTO getPost(Long id) {
        Optional<Post> post = db.getPosts().findById(id);

        PostDTO postDTO = post.map(postMapper::toDTO).orElseThrow(() -> {
            logService.log("ERROR", "Post not found in getPost()", "PostService");
            return new NotFoundException("Post not found with ID: " + id);
        });

        logService.log("INFO", "Post has been fetched successfully.", "PostService");

        return postDTO;
    }

    @Transactional
    public String deletePost(Long id) {
        Optional<Post> post = db.getPosts().findById(id);

        if (post.isPresent()) {
            db.getUpvotes().deleteAllByPostId(id);
            db.getPosts().deleteById(id);
            logService.log("INFO", "Post deleted successfully in deletePost()", "PostService");
            return "Post deleted successfully";
        }
        logService.log("ERROR", "Post not found in deletePost()", "PostService");
        throw new NotFoundException("Post not found with ID: " + id);
    }
}