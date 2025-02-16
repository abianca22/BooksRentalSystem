package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Category;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.CategoryRepository;
import com.example.books_rental.repository.UserRepository;
import com.example.books_rental.validator.Validator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BooksManagementService {
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BooksManagementService(BookRepository bookRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public Book addBook(Book book, Category category, User user) {
        User userById = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("User with id " + user.getId() + " does not exist!"));
        book.setCategory(category);
        book.setStatus(true);
        if (userById.getRole().getName().equals("client")) {
            throw new RuntimeException("Books can only be added by staff members!");
        }
        return bookRepository.save(book);
    }

    public Book getBookById(int id) {
        return bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id " + id + " does not exist!"));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book updateBook(Book book, Category category, User user) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("User with id " + user.getId() + " does not exist!"));
        if (foundUser.getRole().getName().equals("client")) {
            throw new AccessDeniedException("Books can only be updated by staff members!");
        }
        Book foundBook = bookRepository.findById(book.getId()).orElseThrow(() -> new NotFoundException("Book with id " + book.getId() + " does not exist!"));
        if (book.getTitle() != null) {
            foundBook.setTitle(book.getTitle());
        }
        if (book.getAuthor() != null) {
            foundBook.setAuthor(book.getAuthor());
        }
        if (book.getCategory() != null) {
            foundBook.setCategory(book.getCategory());
        }
        if (book.getDescription() != null) {
            foundBook.setDescription(book.getDescription());
        }
        if (category != null) {
            foundBook.setCategory(category);
        }
        if (book.isStatus() != foundBook.isStatus()) {
            foundBook.setStatus(book.isStatus());
        }
        if (book.getExtensionPrice() >= 0) {
            foundBook.setExtensionPrice(book.getExtensionPrice());
        }
        if (book.getRentalPrice() > 0) {
            foundBook.setRentalPrice(book.getRentalPrice());
        }
        Validator.validateObject(foundBook);
        return bookRepository.save(foundBook);
    }

    public void deleteBook(int id, User user) {
        User foundUser = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("User with id " + user.getId() + " does not exist!"));
        if (foundUser.getRole().getName().equals("client")) {
            throw new AccessDeniedException("Books can only be deleted by staff members!");
        }
        Book foundBook = bookRepository.findById(id).orElseThrow(() -> new NotFoundException("Book with id " + id + " does not exist!"));
        bookRepository.delete(foundBook);
    }

    public List<Book> getBooksByCategory(int categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundException("Category with id " + categoryId + " does not exist!"));
        return bookRepository.findAllByCategory(category);
    }

    public List<Book> filterByTitle(String title) {
        return bookRepository.filterByTitle(title);
    }

    public List<Book> filterByAuthor(String author) {
        return bookRepository.filterByAuthor(author);
    }
}
