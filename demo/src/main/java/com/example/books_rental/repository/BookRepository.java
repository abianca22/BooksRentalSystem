package com.example.books_rental.repository;

import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Integer> {
    @Query("""
    SELECT b FROM Book b WHERE b.title like %:title%
    """)
    List<Book> filterByTitle(String title);
    @Query("""
    SELECT b FROM Book b WHERE b.author like %:author%
    """)
    List<Book> filterByAuthor(String author);
    List<Book> findAllByCategory(Category category);
}
