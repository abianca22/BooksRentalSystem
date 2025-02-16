package com.example.books_rental.model.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "books", schema = "booksrental")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Title is mandatory")
    private String title;
    @NotBlank(message = "Author is mandatory")
    private String author;
    @NotBlank(message = "Publishing house is mandatory")
    private String publishingHouse;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @JsonManagedReference
    private Category category;
    private boolean status = true;
    @Positive
    private float rentalPrice;
    @PositiveOrZero
    private float extensionPrice = 0;
    private String description;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    @JsonBackReference
    private List<Rental> rentals;

}
