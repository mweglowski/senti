package org.example.webapi.mappers;

import org.example.data.model.Post;
import org.example.data.model.Upvote;
import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.webapi.contract.UpvoteDTO;
import org.springframework.stereotype.Component;

@Component
public class UpvoteMapper {
    private final DataCatalog db;

    public UpvoteMapper(DataCatalog db) {
        this.db = db;
    }

    public Upvote toEntity(UpvoteDTO upvoteDTO) {
        Upvote upvote = new Upvote();

        Post post = db.getPosts().getReferenceById(upvoteDTO.getPostId());
        upvote.setPost(post);
        User user = db.getUsers().getReferenceById(upvoteDTO.getUserId());
        upvote.setUser(user);
        upvote.setCreatedAt(upvoteDTO.getCreatedAt());

        return upvote;
    }

    public UpvoteDTO toDTO(Upvote upvote) {
        UpvoteDTO upvoteDTO = new UpvoteDTO();

        upvoteDTO.setId(upvote.getId());
        upvoteDTO.setPostId(upvote.getPost().getId());
        upvoteDTO.setUserId(upvote.getUser().getId());
        upvoteDTO.setCreatedAt(upvote.getCreatedAt());

        return upvoteDTO;
    }
}
