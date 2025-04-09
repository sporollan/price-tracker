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
    }

    // Validate token and return claims
    public Claims validateToken(String token) {
        try {
            //    // Parse and validate JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Verify user exists
            if (userRepository.findByEmail(claims.getSubject()) == null) {
                throw new JwtException("User not found");
            }

            return claims;
        }
        catch (ExpiredJwtException e) {
            throw new JwtException("Token expired");
        } catch (JwtException e) {
            throw new JwtException("Invalid token");
        }

    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

}
