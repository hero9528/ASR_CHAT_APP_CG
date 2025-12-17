package com.funo.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest signUpRequest) {
        try {
            User user = authService.signup(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
            String jwt = tokenProvider.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            String jwt = tokenProvider.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

// DTOs for requests and responses

class SignUpRequest {
    private String username;
    private String email;
    private String password;

    // getters and setters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
}

class LoginRequest {
    private String username;
    private String password;

    // getters and setters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}

class AuthResponse {
    private String accessToken;

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    // getter
    public String getAccessToken() { return accessToken; }
}