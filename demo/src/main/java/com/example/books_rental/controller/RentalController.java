package com.example.books_rental.controller;

import com.example.books_rental.dto.CreateRentalDto;
import com.example.books_rental.dto.UpdateCategoryDto;
import com.example.books_rental.dto.UpdateRentalDto;
import com.example.books_rental.dto.UpdateUserDto;
import com.example.books_rental.mapper.RentalMapper;
import com.example.books_rental.model.entities.Book;
import com.example.books_rental.model.entities.Rental;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.service.BooksManagementService;
import com.example.books_rental.service.RentalsManagementService;
import com.example.books_rental.service.UsersManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalsManagementService rentalService;
    private final BooksManagementService bookService;
    private final UsersManagementService userService;
    private final RentalMapper rentalMapper;

    public RentalController(RentalsManagementService rentalService, BooksManagementService bookService, UsersManagementService userService, RentalMapper rentalMapper) {
        this.rentalService = rentalService;
        this.bookService = bookService;
        this.userService = userService;
        this.rentalMapper = rentalMapper;
    }

    @PostMapping
    public ResponseEntity<Rental> create(@RequestBody CreateRentalDto createRentalDto) {
        Rental rental = rentalMapper.toRental(createRentalDto);
        Book book = bookService.getBookById(createRentalDto.getBookId());
        User employee = userService.getUserById(createRentalDto.getEmployeeId());
        User client = userService.getUserById(createRentalDto.getClientId());
        Rental createdRental = rentalService.addRental(rental, book, employee, client);
        return ResponseEntity.created(URI.create("/rentals/" + createdRental.getId()))
                .body(createdRental);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rental> extendRental(@PathVariable Integer id, @RequestBody ObjectNode requestBody) {
        UpdateRentalDto rentalDto = new ObjectMapper().convertValue(requestBody.get("rental"), UpdateRentalDto.class);
        UpdateUserDto requesterDto = new ObjectMapper().convertValue(requestBody.get("requester"), UpdateUserDto.class);
        User employee = rentalDto.getEmployeeId() != null ? userService.getUserById(rentalDto.getEmployeeId()) : null;
        User requester = userService.getUserById(requesterDto.getId());
        Book book = rentalDto.getBookId() != null ? bookService.getBookById(rentalDto.getBookId()) : null;
        if (!id.equals(rentalDto.getId())) {
            throw new RuntimeException("Id from path does not match with id from request");
        }
        Rental rental = rentalMapper.toRental(rentalDto);
        return ResponseEntity.ok().body(rentalService.updateRental(rental, book, employee, requester));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Rental>> getRentalsByClient(@PathVariable Integer clientId, @RequestBody UpdateUserDto userDto) {
        User requester = userService.getUserById(userDto.getId());
        User client = userService.getUserById(clientId);
        return ResponseEntity.ok().body(rentalService.getRentalsByClient(client, requester));
    }
}
