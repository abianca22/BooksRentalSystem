package com.example.books_rental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UpdateRentalDto {
    @NotNull
    private Integer id;
    private LocalDate dueDate;
    @NotNull
    private Integer bookId;
    @NotNull
    private Integer clientId;
    @NotNull
    private Integer employeeId;

    public UpdateRentalDto(Integer id) {
        this.id = id;
    }
}
