package org.example.webapi.service;

import org.example.data.model.Upvote;
import org.example.data.model.User;
import org.example.data.model.Post;
import org.example.data.repository.DataCatalog;
import org.example.webapi.contract.UpvoteDTO;
import org.example.exceptionhandler.exception.NotFoundException;
import org.example.webapi.mappers.UpvoteMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UpvoteService {

    private final DataCatalog db;
    private final UpvoteMapper upvoteMapper;
    private final LogService logService;

    public UpvoteService(DataCatalog db, UpvoteMapper upvoteMapper, LogService logService) {
        this.db = db;
        this.upvoteMapper = upvoteMapper;
        this.logService = logService;
    }

    public UpvoteDTO createUpvoteForPost(UpvoteDTO upvoteDTO) {
        Long userId = upvoteDTO.getUserId();
        Long postId = upvoteDTO.getPostId();

        db.getUsers().findById(userId).orElseThrow(() -> {
            logService.log("ERROR", "User not found in createUpvoteForPost()", "UpvoteService");
            throw new NotFoundException("User not found with ID: " + userId);
        });

        db.getPosts().findById(postId).orElseThrow(() -> {
            logService.log("ERROR", "Post not found in createUpvoteForPost()", "UpvoteService");
            throw new NotFoundException("Post not found with ID: " + postId);
        });

        Upvote upvote = upvoteMapper.toEntity(upvoteDTO);

        Upvote savedUpvote = db.getUpvotes().save(upvote);

        logService.log("INFO", "Upvote has been created for post in createUpvoteForPost()", "UpvoteService");
        return upvoteMapper.toDTO(savedUpvote);
    }

    public List<UpvoteDTO> getAllUpvotes() {
        List<Upvote> upvotes = db.getUpvotes().findAll();
        logService.log("INFO", "Upvotes have been fetched in getAllUpvotes()", "UpvoteService");
        return upvotes.stream()
                .map(upvoteMapper::toDTO)
                .toList();
    }

    public List<UpvoteDTO> getAllUpvotesOfPost(Long id) {
        List<Upvote> upvotes = db.getUpvotes().findAllByPostId(id);
        logService.log("INFO", "Upvotes have been fetched in getAllUpvotes()", "UpvoteService");
        return upvotes.stream()
                .map(upvoteMapper::toDTO)
                .toList();
    }

    public UpvoteDTO getUpvote(Long id) {
        Optional<Upvote> upvote = db.getUpvotes().findById(id);

        return upvote.map(upvoteMapper::toDTO)
                .orElseThrow(() -> {
                    logService.log("ERROR", "Upvote not found in getUpvote()", "UpvoteService");
                    throw new NotFoundException("Upvote not found with ID: " + id);
                });
    }

    public UpvoteDTO getUpvoteByPostIdAndUserId(Long postId, Long userId) {
        Optional<Upvote> upvote = db.getUpvotes().findByPostIdAndUserId(postId, userId);

        return upvote.map(upvoteMapper::toDTO)
                .orElseThrow(() -> {
                    logService.log("ERROR", "Upvote not found in getUpvote()", "UpvoteService");
                    throw new NotFoundException("Upvote not found with postId: " + postId + " and userId: " + userId);
                });
    }

    public String deleteUpvote(Long id) {
        Optional<Upvote> upvote = db.getUpvotes().findById(id);

        if (upvote.isPresent()) {
            db.getUpvotes().deleteById(id);
            logService.log("INFO", "Upvote has been deleted successfully in deleteUpvote()", "UpvoteService");
            return "Upvote has been deleted successfully.";
        }

        logService.log("ERROR", "Upvote not found in deleteUpvote()", "UpvoteService");
        throw new NotFoundException("Upvote not found with ID: " + id);
    }
}
