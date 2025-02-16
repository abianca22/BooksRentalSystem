package com.example.books_rental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Setter
@Getter
@ToString
public class UpdateBookDto {
    @NotNull
    private Integer id;
    private String title;
    private String author;
    private String description;
    private String publishingHouse;
    private Integer categoryId;
    private boolean status;
    private float rentalPrice;
    private float extensionPrice;

    public UpdateBookDto(Integer id) {
        this.id = id;
    }
}
