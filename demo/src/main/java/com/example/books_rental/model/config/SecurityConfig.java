//package com.example.books_rental.model.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.formLogin(form -> form.loginPage("/login").permitAll());
//        httpSecurity.authorizeHttpRequests(c -> c.requestMatchers("/login").permitAll().requestMatchers("/categories").authenticated().anyRequest().authenticated());
//        return httpSecurity.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
//
