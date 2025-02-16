package com.example.books_rental.controller;

import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.BookRepository;
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
public class BookControllerTest {

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Get a book")
    public void TestGetBook() throws Exception {
        final Integer id = 1;

        final Category category = new Category(1, "Fiction", null, "Fiction Books");
        final Book book = new Book(id, "Book Title", "Book Author", null, category, true, 10.0f, 7.0f, null, null);

        when(bookRepository.findById(id))
                .thenReturn(Optional.of(book));

        when(categoryRepository.findById(id))
                .thenReturn(Optional.of(category));

        mockMvc.perform(get("/books/" + id)) // execute call
                .andExpect(status().isOk()); // assertions
    }

    @Test
    @DisplayName("Get all books")
    public void testGetAllBooks() throws Exception {
        final Integer id = 1;
        final Category category = new Category(1, "Fiction", null, "Fiction Books");
        final Book book = new Book(id, "Book Title", "Book Author", null, category, true, 10.0f, 7.0f, null, null);
        final Book otherbook = new Book(id, "Book Different Title", "Book Author", null, category, true, 18.0f, 7.0f, null, null);

        List<Book> books = Arrays.asList(
                book,
                otherbook
        );

        when(bookRepository.findAll()).thenReturn(books);

        mockMvc.perform(get("/books/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Add a new book")
    public void testCreateBook() throws Exception {
        String newBookJson = """
                {
                    "book": {
                        "title": "Ion",
                        "author": "Liviu Rebreanu",
                        "publishingHouse": "Editura",
                        "rentalPrice": 10.5
                    },
                    "requester": {
                        "id": 1
                    }
                }
                """;
        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");
        book.setRentalPrice(10.5f);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newBookJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Update a book")
    public void testUpdateBook() throws Exception {
        String bookJson = """
                {
                    "book": {
                        "title": "Rascoala",
                        "author": "Liviu Rebreanu",
                        "publishingHouse": "Editura",
                        "rentalPrice": 10.5
                    },
                    "requester": {
                        "id": 1
                    }
                }
                """;
        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");


        Book updatedBook = new Book();
        updatedBook.setId(3);
        updatedBook.setTitle("Rascoala");
        updatedBook.setAuthor("Liviu Rebreanu");
        updatedBook.setPublishingHouse("Editura");
        updatedBook.setRentalPrice(10.5f);

        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Delete a book")
    public void testDeleteBook() throws Exception {
        String bookJson = """
                {
                 "id": 1
                }
                """;

        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");

        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(userRepository.findById(1)).thenReturn(Optional.of(new User(1, "admin", "", "", "", "", "", new Role(1, "admin", null), null, null, null)));

        mockMvc.perform(delete("/books/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk());
    }
}