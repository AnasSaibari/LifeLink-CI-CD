package com.app.userService.Controller;

import com.app.userService.DTO.AuthResponse;
import com.app.userService.DTO.LoginRequest;
import com.app.userService.Service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Requête POST /api/auth/login - Tentative de connexion pour: {}", request.getEmailOrUsername());
        AuthResponse response = authService.login(request);
        log.info("Connexion réussie pour: {}", request.getEmailOrUsername());
        return ResponseEntity.ok(response);
    }
}

