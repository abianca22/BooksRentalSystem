package com.example.books_rental.controller;

import com.example.books_rental.dto.ReturnDto;
import com.example.books_rental.dto.UpdateUserDto;
import com.example.books_rental.mapper.ReturnMapper;
import com.example.books_rental.model.entities.Rental;
import com.example.books_rental.model.entities.Return;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.service.RentalsManagementService;
import com.example.books_rental.service.ReturnsManagementService;
import com.example.books_rental.service.UsersManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/returns")
public class ReturnController {
    private final ReturnsManagementService returnsManagementService;
    private final RentalsManagementService rentalsManagementService;
    private final UsersManagementService usersManagementService;
    private final ReturnMapper returnMapper;

    public ReturnController(ReturnsManagementService returnsManagementService, RentalsManagementService rentalsManagementService, UsersManagementService usersManagementService, ReturnMapper returnMapper) {
        this.returnsManagementService = returnsManagementService;
        this.rentalsManagementService = rentalsManagementService;
        this.usersManagementService = usersManagementService;
        this.returnMapper = returnMapper;
    }

    @PostMapping
    public ResponseEntity<Return> create(@RequestBody ObjectNode requestBody) {
        ReturnDto returnDto = new ObjectMapper().convertValue(requestBody.get("return"), ReturnDto.class);
        UpdateUserDto requesterDto = new ObjectMapper().convertValue(requestBody.get("requester"), UpdateUserDto.class);
        Return returnRental = returnMapper.toReturn(returnDto);
        Rental rental = rentalsManagementService.getRentalById(returnDto.getRentalId());
        User employee = usersManagementService.getUserById(returnDto.getEmployeeId());
        User requester = usersManagementService.getUserById(requesterDto.getId());
        Return createdReturn = returnsManagementService.addReturn(returnRental, rental, employee, requester);
        return ResponseEntity.created(URI.create("/returns/" + createdReturn.getId()))
                .body(createdReturn);
    }
}
