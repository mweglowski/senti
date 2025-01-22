package org.example.webapi.controller;

import org.example.webapi.contract.UserDTO;
import org.example.webapi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
        UserDTO createdUser = userService.createUser(userDto);

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getUsers(@RequestParam(required = false) String email) {
        if (email == null) {
            List<UserDTO> users = userService.getUsers();
            return new ResponseEntity<>(users, HttpStatus.FOUND);
        } else {
            UserDTO user = userService.getUserByEmail(email);
            return new ResponseEntity<>(user, HttpStatus.FOUND);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userService.getUser(id);

        return new ResponseEntity<>(user, HttpStatus.FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO newUserData) {
        UserDTO updatedUser = userService.updateUser(id, newUserData);

        return new ResponseEntity<>(updatedUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String successMessage = userService.deleteUser(id);

        return new ResponseEntity<>(successMessage, HttpStatus.OK);
    }
}