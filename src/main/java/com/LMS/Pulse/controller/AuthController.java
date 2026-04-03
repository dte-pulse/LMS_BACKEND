package com.LMS.Pulse.controller;

import com.LMS.Pulse.Dto.AuthRequest;
import com.LMS.Pulse.Dto.RegisterRequest;
import com.LMS.Pulse.Dto.UserDetailsDto;
import com.LMS.Pulse.Dto.UserUpdateDto;
import com.LMS.Pulse.model.User;
import com.LMS.Pulse.repository.UserGroupMappingRepository;
import com.LMS.Pulse.repository.UserRepository;
import com.LMS.Pulse.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @Value("${jwt.cookieName}")
    private String jwtCookieName;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        ResponseCookie jwtCookie = authService.register(request);
        ResponseCookie loggedInCookie = ResponseCookie.from("isLoggedIn", "true")
                .path("/")
                .secure(true)
                .sameSite("None")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, loggedInCookie.toString())
                .body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        ResponseCookie jwtCookie = authService.login(request);
        ResponseCookie loggedInCookie = ResponseCookie.from("isLoggedIn", "true")
                .path("/")
                .secure(true)
                .sameSite("None")
                .build();
    System.out.println(jwtCookie.toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, loggedInCookie.toString())
                .body("Login successful!");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie jwtCookie = ResponseCookie.from(jwtCookieName, "")
                .httpOnly(true).maxAge(0).path("/api")
                .secure(true).sameSite("None").build();

        ResponseCookie loggedInCookie = ResponseCookie.from("isLoggedIn", "")
                .maxAge(0).path("/")
                .secure(true).sameSite("None").build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, loggedInCookie.toString())
                .body("Logout successful!");
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'EMPLOYEE', 'ADMIN')")
    public ResponseEntity<UserDetailsDto> updateProfile(@RequestBody UserUpdateDto dto) {
        return ResponseEntity.ok(authService.updateProfile(dto));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable String username) {
        return ResponseEntity.ok(authService.getUserDetails(username));

    }
}
