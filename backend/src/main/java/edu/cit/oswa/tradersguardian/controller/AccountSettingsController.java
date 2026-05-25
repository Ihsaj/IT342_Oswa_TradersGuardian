package edu.cit.oswa.tradersguardian.controller;

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

    public AccountSettingsController(AccountSettingsService settingsService, AuthService authService) {
        this.settingsService = settingsService;
        this.authService = authService;
    }

    private User resolveUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException("Missing or invalid authorization header");
        }
        try {
            String email = authService.parseEmailFromToken(authHeader.substring(7));
            return authService.getUserByEmail(email);
        } catch (Exception e) {
            throw new InvalidTokenException("Invalid token");
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
