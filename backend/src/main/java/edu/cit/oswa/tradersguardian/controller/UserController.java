package edu.cit.oswa.tradersguardian.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.cit.oswa.tradersguardian.dto.ApiResponse;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.exception.InvalidTokenException;
import edu.cit.oswa.tradersguardian.service.AuthService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"})
public class UserController {
    
    private final AuthService authService;
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<User>> me(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or invalid authorization header");
        }
        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String email = claims.getSubject();
            User user = authService.getUserByEmail(email);
            return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }
}

