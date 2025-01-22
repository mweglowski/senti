package org.example.data.repository;

import org.example.data.model.Upvote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UpvoteRepository extends JpaRepository<Upvote, Long> {
    List<Upvote> findAllByPostId(Long id);
    Optional<Upvote> findByPostIdAndUserId(Long postId, Long userId);
    void deleteAllByUserId(Long id);
    void deleteAllByPostId(Long id);
}
