package org.example.data.repository;

import org.springframework.stereotype.Repository;

@Repository
public class DataCatalog {
    private UserRepository users;
    private PostRepository posts;
    private CommentRepository comments;
    private LogRepository logs;
    private UpvoteRepository upvotes;

    public DataCatalog(UserRepository users, PostRepository posts, CommentRepository comments, LogRepository logs, UpvoteRepository upvotes) {
        this.users = users;
        this.posts = posts;
        this.comments = comments;
        this.logs = logs;
        this.upvotes = upvotes;
    }

    public UserRepository getUsers() {
        return users;
    }

    public PostRepository getPosts() {
        return posts;
    }

    public CommentRepository getComments() {
        return comments;
    }
    public LogRepository getLogs() { return logs; }
    public UpvoteRepository getUpvotes() { return upvotes; }
}