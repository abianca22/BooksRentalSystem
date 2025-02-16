package com.example.books_rental.controller;

import com.example.books_rental.model.entities.*;
import com.example.books_rental.repository.BookRepository;
import com.example.books_rental.repository.RentalRepository;
import com.example.books_rental.repository.ReturnRepository;
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
public class ReturnControllerTest {

    @MockBean
    private RentalRepository rentalRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private ReturnRepository returnRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Add a return")
    public void TestCreateReturn() throws Exception {
        final String returnJson = """
                    {
                        "return": {
                            "rentalId": 1,
                            "employeeId": 1
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
        Return returnBook = new Return(1, rental, LocalDate.now(), admin);
        when(userRepository.findById(1)).thenReturn(Optional.of(admin));
        when(userRepository.findById(2)).thenReturn(Optional.of(client));
        when(bookRepository.findById(3)).thenReturn(Optional.of(book));
        when(rentalRepository.findById(1)).thenReturn(Optional.of(rental));
        when(returnRepository.save(any(Return.class))).thenReturn(returnBook);

        mockMvc.perform(post("/returns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(returnJson))
                .andExpect(status().isCreated()); // assertions
    }


}