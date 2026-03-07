package edu.cit.oswa.tradersguardian.controller;

import org.springframework.web.bind.annotation.*;

import edu.cit.oswa.tradersguardian.dto.LoginRequest;
import edu.cit.oswa.tradersguardian.dto.RegisterRequest;
import edu.cit.oswa.tradersguardian.entity.User;
import edu.cit.oswa.tradersguardian.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

}