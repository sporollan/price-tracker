package com.sporollan.auth_service.web.security;

import com.sporollan.auth_service.data.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${JWT_SECRET_KEY}")
    private String jwtSecret;

    @Value("${JWT_EXPIRATION_TIME_MILLIS}")
    private long jwtExpiration;

    private final UserRepository userRepository;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Generate JWT token
    public String generateToken(String email, String username) {
        return Jwts.builder()
                .setSubject(email)
                .claim("username", username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
        //return Jwts.builder()
        //        .setSubject(email)
        //        .claim("username", username)
        //        .setIssuedAt(new Date())
        //        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        //        .signWith(getSigningKey())
        //        .compact();
                
    }

    // Validate token and return claims
    public Claims validateToken(String token) {
        try {
            System.out.println("CHECKING");
            //    // Parse and validate JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verify user exists
            if (userRepository.findByEmail(claims.getSubject()) == null) {
                System.out.println("USER DOESNOT EXIST");
                throw new JwtException("User not found");
            }

            return claims;
        }
        catch (ExpiredJwtException e) {
            System.out.println("PROBLEMITAS");
            throw new JwtException("Token expired");
        } catch (JwtException e) {
            System.out.println("NOT VALID");
            throw new JwtException("Invalid token");
        }

    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}


//try {

//
//    
//    // Extract token from "Bearer <token>"
//

//
//    // Check expiration
//    if (claims.getExpiration().before(new Date())) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
//    }
//
//    // Verify user exists (optional but recommended)
//    String email = claims.getSubject();
//    User user = userRepository.findByEmail(email);
//    if (user == null) {
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//    }
//
//    // Return user details (optional)
//    return ResponseEntity.ok().build(); // Or return user info
//} catch (JwtException | IllegalArgumentException e) {
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
//}