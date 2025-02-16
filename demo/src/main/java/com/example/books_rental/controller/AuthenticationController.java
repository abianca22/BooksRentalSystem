//package com.example.books_rental.controller;
//
//import com.example.books_rental.dto.UserDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/login")
//public class AuthenticationController {
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping
//    public ResponseEntity<String> login(@RequestBody UserDto userDto) {
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword())
//            );
//            return ResponseEntity.ok("User " + userDto.getUsername() + " logged in!");
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(401).body("Invalid username or password");
//        }
//    }
//}