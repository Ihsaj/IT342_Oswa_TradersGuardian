package edu.cit.oswa.tradersguardian.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "forward:/html/dashboard.html";
    }

    @GetMapping("/login")
    public String login() {
        return "forward:/html/login.html";
    }

    @GetMapping("/register")
    public String register() {
        return "forward:/html/register.html";
    }

}
