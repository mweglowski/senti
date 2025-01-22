package org.example.updater.service;

import org.example.data.model.Comment;
import org.example.data.model.Post;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.exceptionhandler.exception.UpdaterServiceException;
import org.example.postsclient.PostsClient;
import org.example.postsclient.contract.CommentDTO;
import org.example.postsclient.contract.PostDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UpdaterService {
    private final DataCatalog db;
    private final PostsClient postsClient;

    public UpdaterService(DataCatalog db, PostsClient postsClient) {
        this.db = db;
        this.postsClient = postsClient;
    }

    public String updateBySubreddit(String subreddit) {
        try {

            List<Post> postsToSave = new ArrayList<>();
            List<Comment> commentsToSave = new ArrayList<>();
            List<User> usersToSave = new ArrayList<>();

            List<PostDTO> postDTOs = postsClient.fetchPosts(subreddit);

    //        int i = 0;

            for (PostDTO postDTO : postDTOs) {
    //            if (i > 2) break; else i++;

                Post postToSave = new Post();
                String postSourceId = postDTO.getSourceId();

                postToSave.setTitle(postDTO.getTitle());
                postToSave.setContent(postDTO.getContent());
                postToSave.setCreatedAt(postDTO.getCreatedAt());

                List<CommentDTO> commentDTOs = postsClient.fetchComments(subreddit, postSourceId);

                for (CommentDTO commentDTO : commentDTOs) {
                    Comment commentToSave = new Comment();
                    commentToSave.setContent(commentDTO.getContent());
                    commentToSave.setCreatedAt(commentDTO.getCreatedAt());

                    User commentUser = prepareUser(commentDTO.getAuthorUsername());
                    usersToSave.add(commentUser);

                    commentToSave.setUser(commentUser);
                    commentToSave.setPost(postToSave);

                    commentsToSave.add(commentToSave);
                    postToSave.getComments().add(commentToSave);
                }

                User postUser = prepareUser(postDTO.getAuthorUsername());
                postToSave.setUser(postUser);
                usersToSave.add(postUser);

                postsToSave.add(postToSave);
            }

            db.getUsers().saveAll(usersToSave);
            db.getPosts().saveAll(postsToSave);
            db.getComments().saveAll(commentsToSave);

            return "Successfully updated by subreddit: " + subreddit;
        } catch (Exception e) {
            throw new UpdaterServiceException("Error occurred while updating database in UpdaterService.");
        }
    }

    public User prepareUser(String username) {
        User user = db.getUsers().findByUsername(username);

        if (user == null) {
            user = new User();

            user.setUsername(username);
            user.setEmail(username + "@gmail.com");
            user.setPassword(username);
            user.setCreatedAt(LocalDateTime.now());
        }

        return user;
    }
}
