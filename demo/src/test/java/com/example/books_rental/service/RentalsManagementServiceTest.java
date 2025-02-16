package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.*;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalsManagementServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private RentalsManagementService rentalsManagementService;

    private User employee;
    private User client;
    private User otherClient;
    private Book book;
    private Rental rental;
    private Category category;

    @BeforeEach
    void setUp() {
        Role adminRole = new Role(1, "admin", null);
        Role clientRole = new Role(2, "client", null);

        employee = new User(1, "employee", "password", "Employee", "Test", "0700000000", "employee@example.com", adminRole, null, null, null);
        client = new User(2, "client", "password", "Client", "Test", "0710000000","client@example.com", clientRole, null, null, null);
        otherClient = new User(3, "client", "password", "Other", "Client", "0720000000", "email@email.com", clientRole, null, null, null);
        category = new Category(1, "Science", null, "Books related to science.");
        book = new Book(1, "Book Title", "Author", null, category, true, 10.0f, 5.0f,  null, null);

        rental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(14), employee, null, 10.0f);
    }

    @Test
    void testAddRental_whenBookIsAvailable() {
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        Rental addedRental = rentalsManagementService.addRental(rental, book, employee, client);

        assertNotNull(addedRental);
        assertFalse(book.isStatus()); // Cartea devine indisponibilă
        verify(rentalRepository, times(1)).save(rental);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testAddRental_whenBookIsNotAvailable() {
        book.setStatus(false);

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            rentalsManagementService.addRental(rental, book, employee, client);
        });

        assertEquals("This book is not available for rental!", exception.getMessage());
    }

    @Test
    void testGetRentalById_Success() {
        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));

        Rental foundRental = rentalsManagementService.getRentalById(rental.getId());

        assertNotNull(foundRental);
        assertEquals(rental.getId(), foundRental.getId());
        verify(rentalRepository, times(1)).findById(rental.getId());
    }

    @Test
    void testGetRentalById_Fails_NotFound() {
        when(rentalRepository.findById(999)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            rentalsManagementService.getRentalById(999);
        });

        assertEquals("Rental with id 999 does not exist!", exception.getMessage());
    }

    @Test
    void testUpdateRental_Success() {
        Rental updatedRental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(21), employee, null, 15.0f);

        when(rentalRepository.findById(rental.getId())).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        Rental result = rentalsManagementService.updateRental(rental, book, employee, employee);

        assertNotNull(result);
        assertEquals(15.0, result.getTotalPrice());

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository, times(1)).save(rentalCaptor.capture());

        Rental capturedRental = rentalCaptor.getValue();
        assertNotNull(capturedRental);
        assertEquals(updatedRental.getTotalPrice(), capturedRental.getTotalPrice());
        assertEquals(updatedRental.getStartDate(), capturedRental.getStartDate());
        assertEquals(updatedRental.getDueDate(), capturedRental.getDueDate());
    }


    @Test
    void testUpdateRental_Fails_NotFound() {
        Rental updatedRental = new Rental(999, client, book, LocalDate.now(), LocalDate.now().plusDays(21), employee, null, 15.0f);

        when(rentalRepository.findById(updatedRental.getId())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            rentalsManagementService.updateRental(updatedRental, book, employee, employee);
        });

        assertEquals("Rental with id 999 does not exist!", exception.getMessage());
    }

    @Test
    void testGetRentalsByClient_Success() {
        when(rentalRepository.findByClientId(client.getId())).thenReturn(List.of(rental));

        List<Rental> rentals = rentalsManagementService.getRentalsByClient(client, client);

        assertNotNull(rentals);
        assertFalse(rentals.isEmpty());
    }

    @Test
    void testGetRentalsByClient_Fails_AccessDenied() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            rentalsManagementService.getRentalsByClient(client, otherClient);
        });

        assertEquals("Only the staff or the client can view their rentals!", exception.getMessage());
    }

    @Test
    void testCheckEmployee_Success() {
        rentalsManagementService.checkEmployee(employee); // Nu ar trebui să arunce nicio excepție
    }

    @Test
    void testCheckEmployee_Fails() {
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            rentalsManagementService.checkEmployee(client); // Ar trebui să arunce o excepție
        });

        assertEquals("This user is not part of the staff!", exception.getMessage());
    }

    @Test
    void testCheckDate_Success() {
        rentalsManagementService.checkDate(LocalDate.now().plusDays(1)); // Nu ar trebui să arunce nicio excepție
    }

    @Test
    void testCheckDate_Fails_Overdue() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            rentalsManagementService.checkDate(LocalDate.now().minusDays(1)); // Ar trebui să arunce excepția
        });

        assertEquals("The rental is overdue!", exception.getMessage());
    }
}
