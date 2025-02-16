package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.CategoryRepository;
import com.example.books_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BooksManagementServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private BooksManagementService booksManagementService;

    private User adminUser;
    private User clientUser;
    private Category category;
    private Book book;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role(1, "admin", null);
        Role clientRole = new Role(2, "client", null);

        adminUser = new User(1, "adminUser", "password", "Admin", "User", "0712345678", "admin@example.com", adminRole, null, null, null);
        clientUser = new User(2, "clientUser", "password", "Client", "User", "0798765432", "client@example.com", clientRole, null, null, null);

        category = new Category();
        category.setId(1);
        category.setName("Science");
        category.setDescription("Books related to science.");
        book = new Book(1, "Book Title", "Author Name", "Publishing House", category, true, 10.0f, 2.0f, "Description", null);
    }

    @Test
    void testAddBook_Success() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(bookRepository.save(book)).thenReturn(book);

        Book result = booksManagementService.addBook(book, category, adminUser);

        assertNotNull(result);
        assertEquals("Book Title", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testAddBook_Fails_UserNotAuthorized() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.of(clientUser));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            booksManagementService.addBook(book, category, clientUser);
        });

        assertEquals("Books can only be added by staff members!", exception.getMessage());
    }

    @Test
    void testGetBookById_Success() {
        when(bookRepository.findById(1)).thenReturn(Optional.of(book));

        Book result = booksManagementService.getBookById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    void testGetBookById_Fails_NotFound() {
        when(bookRepository.findById(2)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            booksManagementService.getBookById(2);
        });

        assertEquals("Book with id 2 does not exist!", exception.getMessage());
    }

    @Test
    void testDeleteBook_Success() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));

        booksManagementService.deleteBook(book.getId(), adminUser);

        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBook_Fails_UserNotAuthorized() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.of(clientUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            booksManagementService.deleteBook(book.getId(), clientUser);
        });

        assertEquals("Books can only be deleted by staff members!", exception.getMessage());
    }
}
