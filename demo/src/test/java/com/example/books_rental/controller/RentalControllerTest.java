package com.example.books_rental.controller;

import com.example.books_rental.model.entities.*;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.RentalRepository;
import com.example.books_rental.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RentalControllerTest {

    @MockBean
    private RentalRepository rentalRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Add a rental")
    public void TestCreateRental() throws Exception {
        final String rentalJson = """
                {
                    "clientId": 2,
                    "employeeId": 1,
                    "bookId": 3
                }
                """;

        User admin = new User(1, "admin", "rkjakjngjrnkgg", "", "", "0700070001", "email@em.com", new Role(1, "admin", null), null, null, null);
        User client = new User(2, "client", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);
        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");
        book.setRentalPrice(10.5f);
        book.setExtensionPrice(5.0f);
        Rental rental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(14), admin, null, 10.5f);

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2)).thenReturn(Optional.of(client));
        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        mockMvc.perform(post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalJson))
                .andExpect(status().isCreated()); // assertions
    }

    @Test
    @DisplayName("Get rentals by client")
    public void testGetRentals() throws Exception {
        final String rentalJson = """
                {
                    "id": 2
                }
                """;
        User admin = new User(1, "admin", "rkjakjngjrnkgg", "", "", "0700070001", "email@em.com", new Role(1, "admin", null), null, null, null);
        User client = new User(2, "client", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);
        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");
        book.setRentalPrice(10.5f);
        book.setExtensionPrice(5.0f);
        Book otherBook = new Book();
        otherBook.setId(5);
        otherBook.setTitle("Rascoala");
        otherBook.setAuthor("Liviu Rebreanu");
        otherBook.setPublishingHouse("Editura");
        otherBook.setRentalPrice(10.5f);
        otherBook.setExtensionPrice(8.0f);
        Rental rental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(14), admin, null, 10.5f);
        Rental otherRental = new Rental(1, client, otherBook, LocalDate.now(), LocalDate.now().plusDays(14), admin, null, 10.5f);
        List<Rental> rentals = List.of(rental, otherRental);
        when(rentalRepository.findByClientId(2)).thenReturn(rentals);
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2)).thenReturn(Optional.of(client));
        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(bookRepository.findById(5)).thenReturn(Optional.of(otherBook));

        mockMvc.perform(get("/rentals/client/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Update rental")
    public void testUpdateRental() throws Exception {
        final String rentalJson = """
                    {
                        "rental": {
                            "id": 1
                        },
                        "requester": {
                            "id": 1
                        }
                    }
                """;

        User admin = new User(1, "admin", "rkjakjngjrnkgg", "", "", "0700070001", "email@em.com", new Role(1, "admin", null), null, null, null);
        User client = new User(2, "client", "jrgkntgamfmggk", "", "", "0700070000", "email@email.com", new Role(2, "client", null), null, null, null);
        Book book = new Book();
        book.setId(3);
        book.setTitle("Ion");
        book.setAuthor("Liviu Rebreanu");
        book.setPublishingHouse("Editura");
        book.setRentalPrice(10.5f);
        book.setExtensionPrice(5.0f);
        Rental rental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(14), admin, null, 10.5f);

        Rental updatedRental = new Rental(1, client, book, LocalDate.now(), LocalDate.now().plusDays(21), admin, null, 15.5f);

        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2)).thenReturn(Optional.of(client));
        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(updatedRental);

        mockMvc.perform(put("/rentals/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rentalJson))
                .andExpect(status().isOk()); // assertions
    }
}