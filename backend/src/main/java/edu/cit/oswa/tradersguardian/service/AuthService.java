package edu.cit.oswa.tradersguardian.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.cit.oswa.tradersguardian.dto.LoginRequest;
import edu.cit.oswa.tradersguardian.dto.LoginResponse;
import edu.cit.oswa.tradersguardian.dto.RegisterRequest;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.exception.InvalidCredentialsException;
import edu.cit.oswa.tradersguardian.exception.UserAlreadyExistsException;
import edu.cit.oswa.tradersguardian.exception.UserNotFoundException;
import edu.cit.oswa.tradersguardian.repository.UserRepository;

import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        // No expiration — tokens live forever until the user logs out
        String token = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .signWith(key)
                .compact();

        return new LoginResponse(token, "/dashboard");
    }

    /**
     * Extract the email from a JWT token. Works even if the token is expired,
     * because JJWT verifies the signature BEFORE checking expiration.
     * When an ExpiredJwtException is thrown, the claims are still available.
     */
    public String parseEmailFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // Signature was valid but token expired — still trust the claims.
            // This handles old tokens with the 1-hour expiry baked in.
            return e.getClaims().getSubject();
        }
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
