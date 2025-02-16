package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Rental;
import com.example.books_rental.model.entities.Return;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.ReturnRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReturnsManagementService {
    private final ReturnRepository returnRepository;
    private final BookRepository bookRepository;

    public ReturnsManagementService(ReturnRepository returnRepository, BookRepository bookRepository) {
        this.returnRepository = returnRepository;
        this.bookRepository = bookRepository;
    }

    public Return addReturn(Return returnRental, Rental rental, User employee, User requester) {
        if (requester.getRole().getName().equals("client") || (requester.getRole().getName().equals("employee") && requester.getId() != employee.getId())) {
            throw new AccessDeniedException("Only admins or the responsible employee can add the return!");
        }
        returnRental.setRental(rental);
        if (returnRental.getRental().getAssociatedReturn() != null) {
            throw new NotFoundException("The rented book has already been returned!");
        }
        if (returnRental.getRental().getBook() != null) {
            returnRental.getRental().getBook().setStatus(true);
            bookRepository.save(returnRental.getRental().getBook());
        }
        returnRental.setEmployee(employee);
        returnRental.setReturnDate(LocalDate.now());
        return returnRepository.save(returnRental);
    }

}
