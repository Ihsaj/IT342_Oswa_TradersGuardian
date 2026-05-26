package edu.cit.oswa.tradersguardian.dto;

public class LoginResponse {
    private String token;
    private String redirect;

    public LoginResponse(String token, String redirect) {
        this.token = token;
        this.redirect = redirect;
    }

    // Getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
}
