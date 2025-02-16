package com.example.books_rental.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CreateBookDto {
    private String title;
    private String author;
    private String description;
    private String publishingHouse;
    private Integer categoryId;
    private boolean status;
    private float rentalPrice;
    private float extensionPrice;
}
