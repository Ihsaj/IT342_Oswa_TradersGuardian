package com.it342.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @GetMapping("/me")
    public String me(Authentication auth) {
        return auth.getName();
    }
}