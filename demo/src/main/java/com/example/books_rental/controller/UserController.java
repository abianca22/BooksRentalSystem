package com.example.books_rental.controller;

import com.example.books_rental.dto.CreateUserDto;
import com.example.books_rental.dto.UpdateUserDto;
import com.example.books_rental.mapper.UserMapper;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.service.UsersManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "API pentru gestionarea utilizatorilor")
public class UserController {
    private final UsersManagementService userService;
    private final UserMapper userMapper;

    public UserController(UsersManagementService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok().body(userService.getUserByUsername(username));
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserDto userDto) {
        User user = userMapper.toUser(userDto);
        User createdUser = userService.createUser(user);
        return ResponseEntity.created(URI.create("/users/" + user.getId())).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody ObjectNode requestBody) {
        UpdateUserDto userDto = new ObjectMapper().convertValue(requestBody.get("user"), UpdateUserDto.class);
        UpdateUserDto requesterDto = new ObjectMapper().convertValue(requestBody.get("requester"), UpdateUserDto.class);
        User user = userMapper.toUser(userDto);
        User requester = userMapper.toUser(requesterDto);
        if (!id.equals(userDto.getId())) {
            throw new RuntimeException("Id from path does not match with id from request");
        }
        return ResponseEntity.ok().body(userService.updateUser(user, requester));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Integer id, @RequestBody UpdateUserDto userDto) {
        User user = userMapper.toUser(userDto);
        userService.deleteUser(id, user);
        return ResponseEntity.ok().body("User with id " + id + " was deleted successfully!");
    }
}
