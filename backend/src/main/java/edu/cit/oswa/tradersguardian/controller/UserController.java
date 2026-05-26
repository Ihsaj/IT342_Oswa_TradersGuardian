package edu.cit.oswa.tradersguardian.controller;

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

    public UserController(AuthService authService) {
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

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<User>> me(@RequestHeader("Authorization") String authHeader) {
        User user = resolveUser(authHeader);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
    }
}
