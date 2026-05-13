package edu.cit.oswa.tradersguardian.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.cit.oswa.tradersguardian.dto.AccountSettingsRequest;
import edu.cit.oswa.tradersguardian.dto.ApiResponse;
import edu.cit.oswa.tradersguardian.entity.AccountSettings;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.exception.InvalidTokenException;
import edu.cit.oswa.tradersguardian.service.AccountSettingsService;
import edu.cit.oswa.tradersguardian.service.AuthService;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173", "http://127.0.0.1:3000", "http://127.0.0.1:5173"})
public class AccountSettingsController {

    private final AccountSettingsService settingsService;
    private final AuthService authService;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    public AccountSettingsController(AccountSettingsService settingsService, AuthService authService) {
        this.settingsService = settingsService;
        this.authService = authService;
    }

    private User resolveUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or invalid authorization header");
        }
        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
            return authService.getUserByEmail(claims.getSubject());
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid or expired token");
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountSettings>> get(
            @RequestHeader("Authorization") String authHeader) {
        User user = resolveUser(authHeader);
        AccountSettings settings = settingsService.getOrCreate(user);
        return ResponseEntity.ok(ApiResponse.success("Settings fetched", settings));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<AccountSettings>> update(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AccountSettingsRequest request) {
        User user = resolveUser(authHeader);
        AccountSettings settings = settingsService.update(user, request);
        return ResponseEntity.ok(ApiResponse.success("Settings updated", settings));
    }
}
