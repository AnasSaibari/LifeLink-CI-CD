package com.app.userService.Service;

import com.app.userService.DTO.AuthResponse;
import com.app.userService.DTO.LoginRequest;
import com.app.userService.Entity.User;
import com.app.userService.Repository.UserRepository;
import com.app.userService.Security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        log.info("Tentative de connexion pour: {}", request.getEmailOrUsername());
        
        // Récupérer l'utilisateur depuis la base de données (insensible à la casse pour email)
        String emailOrUsername = request.getEmailOrUsername();
        User user = userRepository.findByEmail(emailOrUsername)
                .orElseGet(() -> userRepository.findByEmail(emailOrUsername.toLowerCase())
                        .orElseGet(() -> userRepository.findByEmail(emailOrUsername.toUpperCase())
                                .orElseGet(() -> userRepository.findByUsername(emailOrUsername)
                                        .orElse(null))));

        if (user == null) {
            log.warn("Utilisateur non trouvé: {}", emailOrUsername);
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'email ou username: " + emailOrUsername);
        }

        // Vérifier le mot de passe
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            log.warn("Mot de passe vide pour l'utilisateur: {}", emailOrUsername);
            throw new IllegalArgumentException("Mot de passe non configuré pour cet utilisateur");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Mot de passe incorrect pour l'utilisateur: {}", emailOrUsername);
            throw new IllegalArgumentException("Mot de passe incorrect");
        }

        // Charger les détails de l'utilisateur
        UserDetails userDetails = userDetailsService.loadUserByUsername(
                user.getEmail() != null ? user.getEmail() : user.getUsername()
        );

        // Générer le token JWT avec des claims supplémentaires
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("username", user.getUsername());
        extraClaims.put("role", user.getRole());
        
        String jwtToken = jwtService.generateToken(userDetails, extraClaims);

        log.info("Utilisateur connecté: {}", user.getEmail() != null ? user.getEmail() : user.getUsername());

        return new AuthResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}

