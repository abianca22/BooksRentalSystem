package com.example.books_rental.service;

import com.example.books_rental.exception.AccessDeniedException;
import com.example.books_rental.exception.ExistingDataException;
import com.example.books_rental.exception.NotFoundException;
import com.example.books_rental.model.entities.*;
import com.example.books_rental.repository.RoleRepository;
import com.example.books_rental.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsersManagementServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UsersManagementService usersManagementService;

    private User adminUser;
    private User clientUser;
    private Role adminRole;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        adminRole = new Role(1, "admin", null);
        clientRole = new Role(2, "client", null);

        adminUser = new User(1, "adminUser", "password", "Admin", "User", "0712345678", "admin@example.com", adminRole, null, null, null);
        clientUser = new User(2, "clientUser", "password", "Client", "User", "0798765432", "client@example.com", clientRole, null, null, null);
    }

    @Test
    void testGetUserById_Success() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        User result = usersManagementService.getUserById(adminUser.getId());

        assertNotNull(result);
        assertEquals(adminUser.getId(), result.getId());
        verify(userRepository, times(1)).findById(adminUser.getId());
    }

    @Test
    void testGetUserById_Fails_NotFound() {
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usersManagementService.getUserById(2);
        });

        assertEquals("User with id 2 does not exist!", exception.getMessage());
    }

    @Test
    void testGetUserByUsername_Success() {
        when(userRepository.findUserByUsername(clientUser.getUsername())).thenReturn(Optional.of(clientUser));

        User result = usersManagementService.getUserByUsername(clientUser.getUsername());

        assertNotNull(result);
        assertEquals(clientUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findUserByUsername(clientUser.getUsername());
    }

    @Test
    void testGetUserByUsername_Fails_NotFound() {
        when(userRepository.findUserByUsername("nonExistentUser")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usersManagementService.getUserByUsername("nonExistentUser");
        });

        assertEquals("User with username nonExistentUser does not exist!", exception.getMessage());
    }

    @Test
    void testCreateUser_Success() {
        User newUser = new User(3, "newUser", "password", "New", "User", "0798765433", "newuser@example.com", clientRole, null, null, null);
        when(roleRepository.findByName("client")).thenReturn(Optional.of(clientRole));
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = usersManagementService.createUser(newUser);

        assertNotNull(result);
        assertEquals("newUser", result.getUsername());
        verify(userRepository, times(1)).save(newUser);
    }

    @Test
    void testCreateUser_Fails_RoleNotFound() {
        User newUser = new User(3, "newUser", "password", "New", "User", "0798765433", "newuser@example.com", clientRole, null, null, null);
        when(roleRepository.findByName("client")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usersManagementService.createUser(newUser);
        });

        assertEquals("Role with name 'client' does not exist!", exception.getMessage());
    }

    @Test
    void testUpdateUser_Success() {
        User updatedUser = new User(1, "updatedUser", "newPassword", "Admin", "User", "0712345678", "updated@example.com", adminRole, null, null, null);
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));
        when(userRepository.findById(updatedUser.getId())).thenReturn(Optional.of(updatedUser));
        when(userRepository.save(updatedUser)).thenReturn(updatedUser);

        User result = usersManagementService.updateUser(updatedUser, adminUser);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void testUpdateUser_Fails_AccessDenied() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.of(clientUser));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        adminUser.setUsername("newAdminUser");

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            usersManagementService.updateUser(adminUser, clientUser);
        });

        assertEquals("Users can only be updated by staff members or the user!", exception.getMessage());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        usersManagementService.deleteUser(adminUser.getId(), adminUser);

        verify(userRepository, times(1)).deleteById(adminUser.getId());
    }

    @Test
    void testDeleteUser_Fails_AccessDenied() {
        when(userRepository.findById(clientUser.getId())).thenReturn(Optional.of(clientUser));
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            usersManagementService.deleteUser(adminUser.getId(), clientUser);
        });

        assertEquals("Users can only be deleted by admins or the user!", exception.getMessage());
    }

    @Test
    void testDeleteUser_Fails_UserNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());
        when(userRepository.findById(adminUser.getId())).thenReturn(Optional.of(adminUser));

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            usersManagementService.deleteUser(999, adminUser);
        });

        assertEquals("User with id 999 does not exist!", exception.getMessage());
    }

    @Test
    void testCheckUsername_Success() {
        when(userRepository.findUserByUsername("newUser")).thenReturn(Optional.empty());

        usersManagementService.checkUsername(new User(0, "newUser", "", "", "", "", "", null, null, null, null));

        verify(userRepository, times(1)).findUserByUsername("newUser");
    }

    @Test
    void testCheckUsername_Fails_ExistingUsername() {
        when(userRepository.findUserByUsername("existingUser")).thenReturn(Optional.of(adminUser));

        ExistingDataException exception = assertThrows(ExistingDataException.class, () -> {
            usersManagementService.checkUsername(new User(0, "existingUser", "", "", "", "", "", null, null, null, null));
        });

        assertEquals("User with username existingUser already exists!", exception.getMessage());
    }
}
