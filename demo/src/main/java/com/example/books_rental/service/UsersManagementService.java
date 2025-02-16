package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.ExistingDataException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.Role;
import com.example.books_rental.model.entities.User;
import com.example.books_rental.repository.RoleRepository;
import com.example.books_rental.repository.UserRepository;
import com.example.books_rental.validator.Validator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UsersManagementService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User getUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) {
            throw new NotFoundException("User with id " + id + " does not exist!");
        }
        return user.get();
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isEmpty()) {
            throw new NotFoundException("User with username " + username + " does not exist!");
        }
        return user.get();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        checkUsername(user);
        checkEmail(user);
        checkPhone(user);
        user.setPassword(this.passwordEncoder(user.getPassword()));
        Optional<Role> role = roleRepository.findByName("client");
        if (role.isEmpty()) {
            throw new NotFoundException("Role with name 'client' does not exist!");
        }
        user.setRole(role.get());
        return userRepository.save(user);
    }

    public User updateUser(User user, User requester) {
        Optional<User> foundUser = userRepository.findById(user.getId());
        Optional<User> requesterUser = userRepository.findById(requester.getId());
        if (requesterUser.isEmpty()) {
            throw new NotFoundException("Requester user with id " + requester.getId() + " does not exist!");
        }
        if (requesterUser.get().getRole().getName().equals("client") && requesterUser.get().getId() != user.getId()) {
            throw new AccessDeniedException("Users can only be updated by staff members or the user!");
        }
        if (foundUser.isEmpty()) {
            throw new NotFoundException("User with id " + user.getId() + " does not exist!");
        }
        if (user.getUsername() != null && !user.getUsername().equals(foundUser.get().getUsername())) {
            this.checkUsername(user);
            foundUser.get().setUsername(user.getUsername());
        }
        if (user.getPassword() != null) {
            foundUser.get().setPassword(this.passwordEncoder(user.getPassword()));
        }
        if (user.getRole() != null) {
            foundUser.get().setRole(user.getRole());
        }
        if (user.getEmail() != null) {
            this.checkEmail(user);
            foundUser.get().setEmail(user.getEmail());
        }
        if (user.getLastname() != null) {
            foundUser.get().setLastname(user.getLastname());
        }
        if (user.getFirstname() != null) {
            foundUser.get().setFirstname(user.getFirstname());
        }
        Validator.validateObject(foundUser.get());
        return userRepository.save(foundUser.get());
    }

    public void deleteUser(int id, User requester) {
        Optional<User> user = userRepository.findById(id);
        Optional<User> requesterUser = userRepository.findById(requester.getId());
        if (requesterUser.isEmpty()) {
            throw new NotFoundException("Requester user with id " + requester.getId() + " does not exist!");
        }
        if ((requesterUser.get().getRole().getName().equals("client") && requesterUser.get().getId() != id) || requesterUser.get().getRole().getName().equals("employee")) {
            throw new AccessDeniedException("Users can only be deleted by admins or the user!");
        }
        if(user.isEmpty()) {
            throw new NotFoundException("User with id " + id + " does not exist!");
        }
        userRepository.deleteById(id);
    }

    public String passwordEncoder(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public void checkUsername(User user) {
        Optional<User> alreadyExistingUser = userRepository.findUserByUsername(user.getUsername());
        if (alreadyExistingUser.isPresent() && alreadyExistingUser.get().getId() != user.getId()) {
            throw new ExistingDataException("User with username " + user.getUsername() + " already exists!");
        }
    }

    public void checkEmail(User user) {
        Optional<User> alreadyExistingUser = userRepository.findByEmail(user.getEmail());
        if (alreadyExistingUser.isPresent() && alreadyExistingUser.get().getId() != user.getId()) {
            throw new ExistingDataException("User with email " + user.getEmail() + " already exists!");
        }
    }

    public void checkPhone(User user) {
        Optional<User> alreadyExistingUser = userRepository.findByPhone(user.getPhone());
        if (alreadyExistingUser.isPresent() && alreadyExistingUser.get().getId() != user.getId()) {
            throw new ExistingDataException("User with phone " + user.getPhone() + " already exists!");
        }
    }
}
