package org.example.data.repository;

import org.example.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // custom methods for searching
    User findByUsername(String username);
    Optional<User> findByEmail(String email);
}