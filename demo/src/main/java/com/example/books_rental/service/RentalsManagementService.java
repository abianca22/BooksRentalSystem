package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.InvalidDataException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Rental;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.RentalRepository;
import com.example.books_rental.validator.Validator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class RentalsManagementService {
    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;

    public RentalsManagementService(RentalRepository rentalsRepository, BookRepository bookRepository) {
        this.rentalRepository = rentalsRepository;
        this.bookRepository = bookRepository;
    }

    public Rental addRental(Rental rental, Book book, User employee, User client) {
        if (!book.isStatus()) {
            throw new AccessDeniedException("This book is not available for rental!");
        }
        book.setStatus(false);
        rental.setBook(book);
        checkEmployee(employee);
        rental.setEmployee(employee);
        rental.setClient(client);
        rental.setStartDate(LocalDate.now());
        rental.setDueDate(LocalDate.now().plusDays(14));
        rental.setTotalPrice(book.getRentalPrice());
        bookRepository.save(book);
        return rentalRepository.save(rental);
    }

    public Rental getRentalById(int id) {
        return rentalRepository.findById(id).orElseThrow(() -> new NotFoundException("Rental with id " + id + " does not exist!"));
    }

    public Rental updateRental(Rental rental, Book book, User employee, User requester) {
        Rental foundRental = rentalRepository.findById(rental.getId()).orElseThrow(() -> new NotFoundException("Rental with id " + rental.getId() + " does not exist!"));
        checkRequester(requester, foundRental.getEmployee());
        if (foundRental.getBook() != null) {
            checkDate(foundRental.getDueDate());
            foundRental.setDueDate(foundRental.getDueDate().plusDays(7));
            foundRental.setTotalPrice(foundRental.getTotalPrice() + foundRental.getBook().getExtensionPrice());
        }
        if (employee != null) {
            if (!requester.getRole().getName().equals("admin")) {
                throw new AccessDeniedException("Only admins can update the employee of a rental!");
            }
            checkEmployee(employee);
            foundRental.setEmployee(employee);
        }
        Validator.validateObject(foundRental);
        return rentalRepository.save(foundRental);
    }

    public List<Rental> getRentalsByClient(User client, User requester) {
        if (requester.getRole().getName().equals("client") && requester.getId() != client.getId()) {
            throw new AccessDeniedException("Only the staff or the client can view their rentals!");
        }
        return rentalRepository.findByClientId(client.getId());
    }

    public void checkRequester(User requester, User employee) {
        if (requester.getRole().getName().equals("client") || (requester.getRole().getName().equals("employee") && requester.getId() != employee.getId())) {
            throw new AccessDeniedException("Only admins or the employee responsible for the rental can update it!");
        }
    }


    public void checkEmployee(User employee) {
        if (employee.getRole().getName().equals("client")) {
            throw new AccessDeniedException("This user is not part of the staff!");
        }
    }

    public void checkDate(LocalDate dueDate) {
        if (LocalDate.now().isAfter(dueDate)) {
            throw new RuntimeException("The rental is overdue!");
        }
    }
}
