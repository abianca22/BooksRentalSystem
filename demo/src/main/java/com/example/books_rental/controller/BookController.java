package com.example.books_rental.controller;

import com.example.books_rental.dto.CreateBookDto;
import com.example.books_rental.dto.UpdateBookDto;
import com.example.books_rental.dto.UpdateUserDto;
import com.example.books_rental.mapper.BookMapper;
import com.example.books_rental.mapper.UserMapper;
import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.service.BooksManagementService;
import com.example.books_rental.service.CategoriesManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "API pentru gestionarea cărților")
public class BookController {
    private final BooksManagementService bookService;
    private final CategoriesManagementService categoryService;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public BookController(BooksManagementService bookService, CategoriesManagementService categoryService, BookMapper bookMapper, UserMapper userMapper) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<Book> create(@RequestBody ObjectNode requestBody) {
        CreateBookDto createBookDto = new ObjectMapper().convertValue(requestBody.get("book"), CreateBookDto.class);
        Book book = bookMapper.toBook(createBookDto);
        UpdateUserDto userDto = new ObjectMapper().convertValue(requestBody.get("requester"), UpdateUserDto.class);
        User user = userMapper.toUser(userDto);
        Category category = createBookDto.getCategoryId() != null ? categoryService.getById(createBookDto.getCategoryId()) : null;
        Book createdBook = bookService.addBook(book, category, user);
        return ResponseEntity.created(URI.create("/books/" + createdBook.getId()))
                .body(book);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(@PathVariable Integer id) {
        return ResponseEntity.ok().body(bookService.getBookById(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Iterable<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.getAllBooks());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Integer id, @RequestBody ObjectNode requestBody) {
        UpdateBookDto bookDto = new ObjectMapper().convertValue(requestBody.get("book"), UpdateBookDto.class);
        UpdateUserDto userDto = new ObjectMapper().convertValue(requestBody.get("requester"), UpdateUserDto.class);
        User user = userMapper.toUser(userDto);
        if (!id.equals(bookDto.getId())) {
            throw new RuntimeException("Id from path does not match with id from request");
        }
        Book book = bookMapper.toBook(bookDto);
        Category category = bookDto.getCategoryId() != null ? categoryService.getById(bookDto.getCategoryId()) : null;
        return ResponseEntity.ok().body(bookService.updateBook(book, category, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Integer id, @RequestBody UpdateUserDto userDto) {
        User user = userMapper.toUser(userDto);
        bookService.deleteBook(id, user);
        return ResponseEntity.ok().body("Book with id " + id + " was deleted successfully!");
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Iterable<Book>> getBooksByCategory(@PathVariable Integer id) {
        return ResponseEntity.ok().body(bookService.getBooksByCategory(id));
    }

    @GetMapping("/filter/title/{title}")
    public ResponseEntity<Iterable<Book>> filterByTitle(@PathVariable String title) {
        return ResponseEntity.ok().body(bookService.filterByTitle(title));
    }

    @GetMapping("/filter/author/{author}")
    public ResponseEntity<Iterable<Book>> filterByAuthor(@PathVariable String author) {
        return ResponseEntity.ok().body(bookService.filterByAuthor(author));
    }
}
