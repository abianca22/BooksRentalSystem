//package com.example.books_rental.service;
//
//import com.example.books_rental.repository.UserRepository;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//import com.example.books_rental.model.security.SecurityUser;
//
//@Service
//public class AuthenticationService implements UserDetailsService {
//
//    private final UserRepository userRepository;
//
//    public AuthenticationService(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        final var userFromDb = userRepository.findUserByUsername(username);
//        return userFromDb.map(SecurityUser::new)
//                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " not found!"));
//    }
//}
