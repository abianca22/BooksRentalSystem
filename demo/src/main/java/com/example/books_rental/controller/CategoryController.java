package com.example.books_rental.controller;

import com.example.books_rental.dto.CreateCategoryDto;
import com.example.books_rental.dto.UpdateUserDto;
import com.example.books_rental.dto.UpdateCategoryDto;
import com.example.books_rental.mapper.CategoryMapper;
import com.example.books_rental.mapper.UserMapper;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.service.CategoriesManagementService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@Tag(name = "Categories", description = "API pentru gestionarea categoriilor de cărți")
public class CategoryController {
    private final CategoriesManagementService categoryService;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    public CategoryController(CategoriesManagementService categoryService, CategoryMapper categoryMapper, UserMapper userMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    @Operation(summary = "Adaugă o categorie nouă", description = "Adaugă o categorie nouă în baza de date.")
    public ResponseEntity<Category> create(@RequestBody ObjectNode requestBody) {
            CreateCategoryDto createCategoryDto = new ObjectMapper().convertValue(requestBody.get("category"), CreateCategoryDto.class);
            Category category = categoryMapper.toCategory(createCategoryDto);
        UpdateUserDto userDto = null;
        try {
            userDto = new ObjectMapper().readValue(requestBody.get("user").toString(), UpdateUserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        User user = userMapper.toUser(userDto);
        Category createdCategory = categoryService.addCategory(category, user);
        return ResponseEntity.created(URI.create("/categories/" + createdCategory.getId()))
                    .body(category);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizează o categorie", description = "Actualizează detaliile unei categorii pe baza ID-ului.")
    public ResponseEntity<Category> update(@PathVariable Integer id, @RequestBody ObjectNode requestBody) {
        UpdateCategoryDto categoryDto = new ObjectMapper().convertValue(requestBody.get("category"), UpdateCategoryDto.class);
        UpdateUserDto userDto = new ObjectMapper().convertValue(requestBody.get("user"), UpdateUserDto.class);
        User user = userMapper.toUser(userDto);
        if (!id.equals(categoryDto.getId())) {
            throw new RuntimeException("Id from path does not match with id from request");
        }
        Category category = categoryMapper.toCategory(categoryDto);
        return ResponseEntity.ok().body(categoryService.updateCategory(category, user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Șterge o categorie", description = "Șterge o categorie pe baza ID-ului.")
    public ResponseEntity<String> delete(@PathVariable Integer id, @RequestBody UpdateUserDto userDto) {
        User user = userMapper.toUser(userDto);
        categoryService.deleteCategory(id, user);
        return ResponseEntity.ok().body("Category with id " + id + " was deleted successfully!");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obține o categorie după ID", description = "Returnează detaliile unei categorii pe baza ID-ului.")
    public ResponseEntity<Category> get(@PathVariable Integer id) {
        return ResponseEntity.ok().body(categoryService.getById(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Obține toate categoriile", description = "Returnează toate categoriile din baza de date.")
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok().body(categoryService.getAllCategories());
    }
}
