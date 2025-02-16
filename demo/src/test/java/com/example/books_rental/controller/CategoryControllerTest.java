package com.example.books_rental.controller;

import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.CategoryRepository;
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
public class CategoryControllerTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get a category")
    public void TestGetCategory() throws Exception {
        final Integer id = 1;

        final Category category = new Category();
        category.setId(id);
        category.setName("Fiction");

        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));

        mockMvc.perform(get("/categories/" + id)) // execute call
                .andExpect(status().isOk()); // assertions
    }

    @Test
    @DisplayName("Get all categories")
    public void testGetAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(
                new Category(1, "Fiction", null, "Fiction Books"),
                new Category(2, "Science", null, "Science Books")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        mockMvc.perform(get("/categories/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Create a new category")
    public void testCreateCategory() throws Exception {
        String newCategoryJson = """
                {
                 "category": {
                    "name": "History",
                    "description": "History Books"
                 },
                 "user": {
                    "id": 1
                 }
                }
                """;
        Category category = new Category();
        category.setId(3);
        category.setName("History");
        category.setDescription("History Books");


        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update a category")
    public void testUpdateCategory() throws Exception {
        String categoryJson = """
                {
                 "category": {
                    "id": 3,
                    "name": "History Updated",
                    "description": "History Books"
                 },
                 "user": {
                    "id": 1
                 }
                }
                """;
        Category category = new Category();
        category.setId(3);
        category.setName("History");
        category.setDescription("History Books");

        Category updatedcategory = new Category();
        updatedcategory.setId(3);
        updatedcategory.setName("History Updated");
        updatedcategory.setDescription("History Books");

        when(categoryRepository.findById(3)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedcategory);
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(put("/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("History Updated"));
    }

    @Test
    @DisplayName("Delete a category")
    public void testDeleteCategory() throws Exception {
        String categoryJson = """
                {
                 "id": 1
                }
                """;
        Category category = new Category();
        category.setId(3);
        category.setName("History");
        category.setDescription("History Books");

        when(categoryRepository.findById(3)).thenReturn(Optional.of(category));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(delete("/categories/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(categoryJson))
                .andExpect(status().isOk());
    }
}