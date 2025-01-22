package org.example.webapi.service;

import org.example.data.model.User;
import org.example.data.repository.DataCatalog;
import org.example.webapi.contract.UserDTO;
import org.example.exceptionhandler.exception.NotFoundException;
import org.example.webapi.mappers.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final DataCatalog db;
    private final UserMapper userMapper;
    private final LogService logService;

    public UserService(DataCatalog db, UserMapper userMapper, LogService logService) {
        this.logService = logService;
        this.userMapper = userMapper;
        this.db = db;
    }

    public UserDTO createUser(UserDTO userDto) {
        User user = userMapper.toEntity(userDto);

        User savedUser = db.getUsers().save(user);

        logService.log("INFO", "User has been created in createUser()", "UserService");

        return userMapper.toDTO(savedUser);
    }

    public List<UserDTO> getUsers() {
        List<User> users = db.getUsers().findAll();

        logService.log("INFO", "Users have been fetched in getUsers()", "UserService");

        return users
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    public UserDTO getUser(Long id) {
        Optional<User> user = db.getUsers().findById(id);

        return user.map(userMapper::toDTO)
                .orElseThrow(() -> {
                    logService.log("ERROR", "User not found in getUser()", "UserService");
                    throw new NotFoundException("User not found with ID: " + id);
                });
    }

    public UserDTO getUserByEmail(String email) {
        Optional<User> user = db.getUsers().findByEmail(email);

        return user.map(userMapper::toDTO)
                .orElseThrow(() -> {
                    logService.log("ERROR", "User not found in getUser()", "UserService");
                    throw new NotFoundException("User not found with email: " + email);
                });
    }

    public UserDTO updateUser(Long id, UserDTO newUserData) {
        Optional<User> user = db.getUsers().findById(id);

        if (user.isPresent()) {
            User existingUser = user.get();

            existingUser.setUsername(newUserData.getUsername());
            existingUser.setEmail(newUserData.getEmail());
            existingUser.setPassword(newUserData.getPassword());
            existingUser.setCreatedAt(newUserData.getCreatedAt());

            logService.log("INFO", "User has been updated successfully in updateUser()", "UserService");
            db.getUsers().save(existingUser);
            return userMapper.toDTO(existingUser);
        }
        logService.log("ERROR", "User not found in updateUser()", "UserService");
        throw new NotFoundException("User not found with ID: " + id);
    }

    @Transactional
    public String deleteUser(Long id) {
        Optional<User> user = db.getUsers().findById(id);

        if (user.isPresent()) {
            db.getUpvotes().deleteAllByUserId(id);
            db.getUsers().deleteById(id);
            logService.log("INFO", "User has been deleted successfully in deleteUser()", "UserService");
            return "User has been deleted successfully.";
        }
        logService.log("ERROR", "User not found in deleteUser()", "UserService");
        throw new NotFoundException("User not found with ID: " + id);
    }
}