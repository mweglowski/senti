package org.example.webapi.controller;

import org.example.webapi.contract.UpvoteDTO;
import org.example.webapi.service.UpvoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/upvotes")
public class UpvoteController {
    private final UpvoteService upvoteService;

    public UpvoteController(UpvoteService upvoteService) {
        this.upvoteService = upvoteService;
    }

    @PostMapping
    public ResponseEntity<UpvoteDTO> createUpvote(@RequestBody UpvoteDTO upvoteDTO) {
        UpvoteDTO createdUpvote = upvoteService.createUpvoteForPost(upvoteDTO);

        return new ResponseEntity<>(createdUpvote, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getUpvotes(
            @RequestParam(required = false) Long postId,
            @RequestParam(required = false) Long userId) {

        if (postId != null && userId != null) {

            UpvoteDTO upvoteDTO = upvoteService.getUpvoteByPostIdAndUserId(postId, userId);

            return new ResponseEntity<>(upvoteDTO, HttpStatus.OK);
        } else if (postId != null) {

            List<UpvoteDTO> upvoteDTOs = upvoteService.getAllUpvotesOfPost(postId);

            return new ResponseEntity<>(upvoteDTOs, HttpStatus.OK);
        } else {

            List<UpvoteDTO> upvoteDTOs = upvoteService.getAllUpvotes();

            return new ResponseEntity<>(upvoteDTOs, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UpvoteDTO> getUpvote(@PathVariable Long id) {
        UpvoteDTO upvoteDTO = upvoteService.getUpvote(id);

        return new ResponseEntity<>(upvoteDTO, HttpStatus.FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUpvote(@PathVariable Long id) {
        String successMessage = upvoteService.deleteUpvote(id);

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }
}
