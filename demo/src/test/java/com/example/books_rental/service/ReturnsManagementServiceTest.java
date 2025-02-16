package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.*;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.ReturnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReturnsManagementServiceTest {

    @Mock
    private ReturnRepository returnRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReturnsManagementService returnsManagementService;

    private User employee;
    private User client;
    private Rental rental;
    private Book book;
    private Return returnRental;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new User();
        employee.setId(1);
        employee.setUsername("employee");
        employee.setRole(new Role(3, "employee", null));

        client = new User();
        client.setId(2);
        client.setUsername("client");
        client.setRole(new Role(2, "client", null));

        book = new Book();
        book.setId(1);
        book.setTitle("Book Title");
        book.setAuthor("Author");
        book.setRentalPrice(10.0f);
        book.setStatus(true);

        rental = new Rental();
        rental.setId(1);
        rental.setBook(book);
        rental.setClient(client);
        rental.setEmployee(employee);
        rental.setStartDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(14));
        rental.setTotalPrice(book.getRentalPrice());

        returnRental = new Return();
        returnRental.setId(1);
        returnRental.setRental(rental);
        returnRental.setEmployee(employee);
    }

    @Test
    public void testAddReturn_Success() {
        when(returnRepository.save(any(Return.class))).thenReturn(returnRental);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Return savedReturn = returnsManagementService.addReturn(returnRental, rental, employee, employee);

        assertNotNull(savedReturn);
        assertEquals(returnRental.getId(), savedReturn.getId());
        assertTrue(book.isStatus()); // Cartea trebuie să fie acum disponibilă
    }

    @Test
    public void testAddReturn_AccessDenied() {
        when(returnRepository.save(any(Return.class))).thenReturn(returnRental);

        // Test dacă un client încearcă să adauge un return
        AccessDeniedException thrown = assertThrows(AccessDeniedException.class, () ->
                returnsManagementService.addReturn(returnRental, rental, employee, client)
        );
        assertEquals("Only admins or the responsible employee can add the return!", thrown.getMessage());
    }

    @Test
    public void testAddReturn_AlreadyReturned() {
        when(returnRepository.save(any(Return.class))).thenReturn(returnRental);

        // Să setăm deja Return-ul pentru rental
        rental.setAssociatedReturn(new Return());

        NotFoundException thrown = assertThrows(NotFoundException.class, () ->
                returnsManagementService.addReturn(returnRental, rental, employee, employee)
        );
        assertEquals("The rented book has already been returned!", thrown.getMessage());
    }
}
