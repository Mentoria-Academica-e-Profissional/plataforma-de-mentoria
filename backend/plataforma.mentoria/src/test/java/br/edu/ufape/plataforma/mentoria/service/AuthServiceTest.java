package br.edu.ufape.plataforma.mentoria.service;

import br.edu.ufape.plataforma.mentoria.dto.UserDTO;
import br.edu.ufape.plataforma.mentoria.enums.UserRole;
import br.edu.ufape.plataforma.mentoria.model.User;
import br.edu.ufape.plataforma.mentoria.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "encodedPass", UserRole.MENTOR);
    }

    // --- loadUserByUsername ---
    @Test
    void loadUserByUsername_ShouldReturnUser_WhenUserExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        UserDetails result = authService.loadUserByUsername("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    void loadUserByUsername_ShouldReturnNull_WhenUserDoesNotExist() {
        when(userRepository.findByEmail("notfound@example.com")).thenReturn(null);

        UserDetails result = authService.loadUserByUsername("notfound@example.com");

        assertNull(result);
    }

    // --- register ---
    @Test
    void register_ShouldSaveUser_WhenEmailNotExists() {
        UserDTO dto = new UserDTO("new@example.com", "MyPassword@1", UserRole.MENTOR);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(null);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authService.register(dto);

        assertNotNull(result);
        assertEquals("new@example.com", result.getUsername());
        assertEquals("hashedPass", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowConflict_WhenEmailExists() {
        UserDTO dto = new UserDTO("test@example.com", "MyPassword@1", UserRole.MENTOR);

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(user);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.register(dto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    // --- getCurrentUser ---
    @Test
    void getCurrentUser_ShouldReturnUser_WhenAuthenticated() {
        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(new TestingAuthenticationToken("test@example.com", null));

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User result = authService.getCurrentUser();

        assertNotNull(result);
        assertEquals("test@example.com", result.getUsername());
    }

    @Test
    void getCurrentUser_ShouldThrowUnauthorized_WhenNoAuthentication() {
        // simula um authentication sem nome
        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(new TestingAuthenticationToken(null, null));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> authService.getCurrentUser());

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}

