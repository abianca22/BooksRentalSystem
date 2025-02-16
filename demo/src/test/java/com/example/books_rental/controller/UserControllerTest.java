package com.example.books_rental.controller;

import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get a user")
    public void TestGetUser() throws Exception {
        final Integer id = 1;

        final User user = new User();
        user.setId(id);
        user.setUsername("admin");

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/" + id)) // execute call
                .andExpect(status().isOk()); // assertions
    }

    @Test
    @DisplayName("Get all users")
    public void testGetAllUsers() throws Exception {
        List<User> users = Arrays.asList(
                new User(1, "admin", "frkjfnkjv", "", "", "", "", new Role(1, "admin", null), null, null, null),
                new User(2, "client", "aabbccddeeffgghh", "", "", "", "", new Role(2, "client", null), null, null, null)
        );

        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
//
    @Test
    @DisplayName("Create a new user")
    public void testCreateUser() throws Exception {
        String newUserJson = """
                {
                    "username": "admin",
                    "password": "admin",
                    "email": "email@email.com",
                    "phone": "0734567890"
                }
                """;
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("admin");
        user.setEmail("email@email.com");
        user.setPhone("0734567890");

        when(userRepository.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update a user")
    public void testUpdateUser() throws Exception {
        String userJson = """
                {
                 "user": {
                    "id": 2,
                    "username": "clientt"
                 },
                 "requester": {
                    "id": 1
                 }
                }
                """;
        User admin = new User(1, "admin", "rkjakjngjrnkgg", "", "", "0700070001", "email@em.com", new Role(1, "admin", null), null, null, null);
        User client = new User(2, "client", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);
        User updatedUser = new User(2, "clientt", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);

        when(userRepository.findById(2)).thenReturn(Optional.of(client));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));

        mockMvc.perform(put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("clientt"));
    }

    @Test
    @DisplayName("Delete a user")
    public void testDeleteUser() throws Exception {
        String categoryJson = """
                {
                 "id": 1
                }
                """;

        User admin = new User(1, "admin", "rkjakjngjrnkgg", "", "", "0700070001", "email@em.com", new Role(1, "admin", null), null, null, null);
        User client = new User(2, "client", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2)).thenReturn(Optional.of(client));

        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk());
    }
}