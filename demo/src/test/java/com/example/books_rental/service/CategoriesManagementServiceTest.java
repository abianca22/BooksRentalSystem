package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.ExistingDataException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.repository.CategoryRepository;
import com.example.books_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriesManagementServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoriesManagementService categoriesManagementService;

    private User adminUser;
    private User clientUser;
    private Category category;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role();
        adminRole.setId(1);
        adminRole.setName("admin");

        Role clientRole = new Role();
        clientRole.setId(2);
        clientRole.setName("client");

        adminUser = new User();
        adminUser.setId(1);
        adminUser.setRole(adminRole);

        clientUser = new User();
        clientUser.setId(2);
        clientUser.setRole(clientRole);

        category = new Category();
        category.setId(1);
        category.setName("Science");
        category.setDescription("Books related to science.");
    }

    @Test
    void testAddCategory_Success() {
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.empty());
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(categoryRepository.save(category)).thenReturn(category);

        Category result = categoriesManagementService.addCategory(category, adminUser);

        assertNotNull(result);
        assertEquals("Science", result.getName());
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void testAddCategory_Fails_UserNotFound() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoriesManagementService.addCategory(category, clientUser);
        });

        assertEquals("User with id 2 does not exist!", exception.getMessage());
    }

    @Test
    void testAddCategory_Fails_UserNotAuthorized() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.of(clientUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            categoriesManagementService.addCategory(category, clientUser);
        });

        assertEquals("Categories can only be added by staff members!", exception.getMessage());
    }

    @Test
    void testAddCategory_Fails_CategoryAlreadyExists() {
        when(categoryRepository.findByName(category.getName())).thenReturn(Optional.of(category));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        ExistingDataException exception = assertThrows(ExistingDataException.class, () -> {
            categoriesManagementService.addCategory(category, adminUser);
        });

        assertEquals("Category with name Science already exists!", exception.getMessage());
    }

    @Test
    void testGetById_Success() {
        when(categoryRepository.findById(1)).thenReturn(Optional.of(category));

        Category result = categoriesManagementService.getById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetById_Fails_NotFound() {
        when(categoryRepository.findById(2)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoriesManagementService.getById(2);
        });

        assertEquals("Category with id 2 does not exist!", exception.getMessage());
    }
}
