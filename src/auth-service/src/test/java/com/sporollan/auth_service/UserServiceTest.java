package com.sporollan.auth_service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.sporollan.auth_service.data.UserRepository;
import com.sporollan.auth_service.exceptions.CustomExceptions.InvalidCredentialsException;
import com.sporollan.auth_service.exceptions.CustomExceptions.UserAlreadyExistsException;
import com.sporollan.auth_service.model.User;
import com.sporollan.auth_service.model.UserCreate;
import com.sporollan.auth_service.model.UserLogin;
import com.sporollan.auth_service.web.UserService;
import com.sporollan.auth_service.web.security.JwtService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    private String testSecret = "abcdefghijklmnopqrsabcdefghijklmnopqrstuvwxyz123456tuvwxyz123456";

    @BeforeEach
    public void setup() {
        // Initialize JwtService with test values
        JwtService jwtService = new JwtService(userRepository);
        ReflectionTestUtils.setField(jwtService, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L);

        // Initialize UserService with dependencies
        userService = new UserService(userRepository);
        
        // Inject dependencies via reflection
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(userService, "jwtService", jwtService);
    }

    @Test
    public void testRegisterUserAccount_Success() {
        // Arrange
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("test@example.com");
        userCreate.setUsername("testuser");
        userCreate.setPassword("password123");

        User savedUser = new User();
        savedUser.setEmail("test@example.com");
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        ResponseEntity<User> response = userService.registerUserAccount(userCreate);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test@example.com", response.getBody().getEmail());
        assertEquals("testuser", response.getBody().getUsername());
        assertEquals("encodedPassword", response.getBody().getPassword());
    

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegisterUserAccount_UserAlreadyExists() {
        // Arrange
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("existing@example.com");
        userCreate.setUsername("existinguser");
        userCreate.setPassword("password123");

        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setUsername("existinguser");

        when(userRepository.findByEmail("existing@example.com")).thenReturn(existingUser);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUserAccount(userCreate);
        });

        verify(userRepository).findByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testRegisterUserAccount_DataIntegrityViolation() {
        // Arrange
        UserCreate userCreate = new UserCreate();
        userCreate.setEmail("test@example.com");
        userCreate.setUsername("testuser");
        userCreate.setPassword("password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.registerUserAccount(userCreate);
        });

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testLoginUserAccount_Success() {
        // Arrange
        UserLogin userLogin = new UserLogin();
        userLogin.setEmail("test@example.com");
        userLogin.setPassword("password123");

        User foundUser = new User();
        foundUser.setEmail("test@example.com");
        foundUser.setUsername("testuser");
        foundUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(foundUser);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        // Act
        ResponseEntity<String> response = userService.loginUserAccount(userLogin);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("password123", "encodedPassword");
    }

    @Test
    public void testLoginUserAccount_UserNotFound() {
        // Arrange
        UserLogin userLogin = new UserLogin();
        userLogin.setEmail("nonexistent@example.com");
        userLogin.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(null);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUserAccount(userLogin);
        });

        verify(userRepository).findByEmail("nonexistent@example.com");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    public void testLoginUserAccount_InvalidPassword() {
        // Arrange
        UserLogin userLogin = new UserLogin();
        userLogin.setEmail("test@example.com");
        userLogin.setPassword("wrongpassword");

        User foundUser = new User();
        foundUser.setEmail("test@example.com");
        foundUser.setUsername("testuser");
        foundUser.setPassword("encodedPassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(foundUser);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            userService.loginUserAccount(userLogin);
        });

        verify(userRepository).findByEmail("test@example.com");
        verify(passwordEncoder).matches("wrongpassword", "encodedPassword");
    }

@Test
public void testValidateToken_ValidToken() {
    // Arrange: Valid token for existing user
    User user = new User();
    user.setEmail("test@example.com");
    user.setUsername("testuser");
    
    String token = Jwts.builder()
        .setSubject("test@example.com")
        .claim("username", "testuser")
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 3600000L))
        .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
        .compact();

    when(userRepository.findByEmail("test@example.com")).thenReturn(user);

    // Act
    ResponseEntity<?> response = userService.validateToken("Bearer " + token);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(userRepository).findByEmail("test@example.com");
}

@Test
public void testValidateToken_ExpiredToken() {
    // Arrange: Expired token
    String expiredToken = Jwts.builder()
        .setSubject("test@example.com")
        .setExpiration(new Date(System.currentTimeMillis() - 1000)) // Expired 1s ago
        .signWith(Keys.hmacShaKeyFor(testSecret.getBytes()))
        .compact();

    // Act
    ResponseEntity<?> response = userService.validateToken("Bearer " + expiredToken);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Token expired", response.getBody());
}

@Test
public void testValidateToken_InvalidSignature() {
    // Arrange: Token signed with wrong secret
    String invalidToken = Jwts.builder()
        .setSubject("test@example.com")
        .signWith(Keys.hmacShaKeyFor("wroabcdefghijklmnopqrstuvwxyz123456ng-secabcdefghijklmnopqrstuvwxyz123456ret".getBytes()))
        .compact();

    // Act
    ResponseEntity<?> response = userService.validateToken("Bearer " + invalidToken);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid token", response.getBody());
}

@Test
public void testValidateToken_MissingUser() {
    // Arrange: Valid token but user not in DB
    String token = Jwts.builder()
        .setSubject("nonexistent@example.com")
        .claim("username", "testuser")
        .setExpiration(new Date(System.currentTimeMillis() + 3600000L)) // Add expiration
        .signWith(Keys.hmacShaKeyFor(testSecret.getBytes())) // Use 64-byte secret
        .compact();

    // Act
    ResponseEntity<?> response = userService.validateToken("Bearer " + token);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertEquals("Invalid token", response.getBody());
}

@Test
public void testValidateToken_MissingAuthHeader() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
        userService.validateToken(null); // Missing header
    });
}
}