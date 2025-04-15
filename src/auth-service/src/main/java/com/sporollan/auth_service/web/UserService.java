package com.sporollan.auth_service.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.sporollan.auth_service.data.UserRepository;
import com.sporollan.auth_service.exceptions.CustomExceptions;
import com.sporollan.auth_service.model.User;
import com.sporollan.auth_service.model.UserCreate;
import com.sporollan.auth_service.model.UserLogin;
import com.sporollan.auth_service.web.security.JwtService;

import io.jsonwebtoken.JwtException;


@RestController
public class UserService {


    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/user")
    public ResponseEntity<User> registerUserAccount(
        @RequestBody UserCreate entity) {
            try {
                // Check if user already exists
                if (userRepository.findByEmail(entity.getEmail()) != null) {
                    throw new CustomExceptions.UserAlreadyExistsException(
                        "User with email " + entity.getEmail() + " already exists"
                    );
                }

                // Create new user object
                User newUser = new User();
                newUser.setEmail(entity.getEmail());
                newUser.setUsername(entity.getUsername());
                newUser.setPassword(passwordEncoder.encode(entity.getPassword()));
                
                // Save the new user to the database
                User savedUser = userRepository.save(newUser);

                return ResponseEntity.ok(savedUser);
            } catch (DataIntegrityViolationException e) {
                throw new CustomExceptions.UserAlreadyExistsException("User already exists with the same email address");
            }
        }

    // Login, return JWT token
    @PostMapping("/login")
    public ResponseEntity<String> loginUserAccount(
        @RequestBody UserLogin entity) {
        try {
            // Find user by email
            User user = userRepository.findByEmail(entity.getEmail());

            if (user == null || !passwordEncoder.matches(entity.getPassword(), user.getPassword())) {
                throw new CustomExceptions.InvalidCredentialsException("Invalid email or password");
            }

            // Generate JWT token
            String token = jwtService.generateToken(user.getEmail(), user.getUsername());
            return ResponseEntity.ok(token);
        } catch (JwtException e) {
            throw new RuntimeException("Error generating JWT token "+ e.getMessage());
        }
    }
    // Validate JWT token and return claims
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Validate header format
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalArgumentException("Missing or invalid Authorization header");
            }
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            jwtService.validateToken(token); // Full validation via JwtService
            return ResponseEntity.ok().build();
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


}