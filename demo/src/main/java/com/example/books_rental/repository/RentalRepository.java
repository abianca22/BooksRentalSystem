package com.example.books_rental.repository;

import com.example.books_rental.model.entities.Rental;
import com.example.books_rental.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalRepository extends JpaRepository<Rental, Integer> {
    @Query("""
    SELECT r FROM Rental r WHERE r.client.id = :clientId
    """)
    List<Rental> findByClientId(Integer clientId);
    @Query("""
    SELECT r FROM Rental r WHERE r.employee.id = :employeeId
    """)
    List<Rental> findByEmployeeId(Integer employeeId);
}
