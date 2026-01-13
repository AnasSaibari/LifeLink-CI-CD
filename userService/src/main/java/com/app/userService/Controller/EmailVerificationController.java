package com.app.userService.Controller;

import com.app.userService.Service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/email-verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService verificationService;

    // ================= SEND CODE =================
    @PostMapping("/send")
    public ResponseEntity<?> sendCode(@RequestParam String email) {
        try {
            log.info("Requête d'envoi de code pour : {}", email);

            // Validation de l'email
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "L'email est requis"
                        ));
            }

            // Validation format email
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "Format d'email invalide"
                        ));
            }

            verificationService.sendVerificationCode(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Code de vérification envoyé avec succès",
                    "email", email
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation lors de l'envoi du code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (RuntimeException e) {
            log.error("Erreur d'envoi d'email", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de l'envoi du code", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Une erreur inattendue s'est produite"
                    ));
        }
    }

    // ================= VERIFY CODE =================
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {
        try {
            log.info("Requête de vérification de code pour : {}", email);

            // Validation des paramètres
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "L'email est requis"
                        ));
            }

            if (code == null || code.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "Le code est requis"
                        ));
            }

            // Validation du format du code (6 chiffres)
            if (!code.matches("^\\d{6}$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "Le code doit contenir 6 chiffres"
                        ));
            }

            boolean verified = verificationService.verifyCode(email, code);

            if (verified) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Email vérifié avec succès",
                        "email", email,
                        "verified", true
                ));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "error", "Vérification échouée"
                    ));

        } catch (IllegalArgumentException e) {
            log.warn("Erreur de validation lors de la vérification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la vérification", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Une erreur inattendue s'est produite"
                    ));
        }
    }

    // ================= RESEND CODE =================
    @PostMapping("/resend")
    public ResponseEntity<?> resendCode(@RequestParam String email) {
        try {
            log.info("Requête de renvoi de code pour : {}", email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "L'email est requis"
                        ));
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "Format d'email invalide"
                        ));
            }

            verificationService.sendVerificationCode(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Nouveau code de vérification envoyé",
                    "email", email
            ));

        } catch (IllegalArgumentException e) {
            log.warn("Erreur lors du renvoi du code: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (RuntimeException e) {
            log.error("Erreur d'envoi d'email", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Erreur lors du renvoi du code", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Une erreur inattendue s'est produite"
                    ));
        }
    }

    // ================= CHECK VERIFICATION STATUS =================
    @GetMapping("/status")
    public ResponseEntity<?> checkStatus(@RequestParam String email) {
        try {
            log.info("Vérification du statut pour : {}", email);

            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "L'email est requis"
                        ));
            }

            boolean verified = verificationService.isEmailVerified(email);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "email", email,
                    "verified", verified
            ));

        } catch (Exception e) {
            log.error("Erreur lors de la vérification du statut", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "success", false,
                            "error", "Une erreur inattendue s'est produite"
                    ));
        }
    }
}