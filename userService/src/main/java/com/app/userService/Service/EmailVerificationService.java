package com.app.userService.Service;

import com.app.userService.Entity.EmailVerification;
import com.app.userService.Repository.EmailVerificationRepository;
import com.app.userService.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final EmailVerificationRepository verificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    // ================== SEND CODE ==================
    @Transactional
    public void sendVerificationCode(String email) {
        log.info("Génération du code de vérification pour : {}", email);

        // Validation de l'email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        // Vérifier si l'email existe déjà dans la base
        if (userRepository.existsByEmail(email)) {
            log.warn("Tentative d'inscription avec un email déjà existant : {}", email);
            throw new IllegalArgumentException("Un compte existe déjà avec cet email");
        }

        // Invalider les anciens codes non vérifiés pour cet email
        verificationRepository.findByEmailAndVerifiedFalse(email)
                .ifPresent(oldCode -> {
                    log.info("Suppression de l'ancien code non vérifié pour : {}", email);
                    verificationRepository.delete(oldCode);
                });

        // Génération du nouveau code à 6 chiffres
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Création de l'entité de vérification
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setCode(code);
        verification.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        verification.setVerified(false);

        verificationRepository.save(verification);
        log.info("Code de vérification sauvegardé pour : {}", email);

        // Envoi du mail
        try {
            sendVerificationEmail(email, code);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email à : {}", email, e);
            // Supprimer la vérification si l'envoi échoue
            verificationRepository.delete(verification);
            throw new RuntimeException("Erreur lors de l'envoi de l'email. Veuillez réessayer.");
        }
    }

    // ================== SEND EMAIL ==================
    private void sendVerificationEmail(String email, String code) {
        log.info("Envoi du code de vérification par email à : {}", email);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Vérification de votre compte");
        message.setText(
                "Bienvenue !\n\n" +
                        "Votre code de vérification est : " + code + "\n\n" +
                        "Ce code expirera dans 10 minutes.\n\n" +
                        "Si vous n'avez pas demandé ce code, ignorez ce message.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe"
        );

        mailSender.send(message);
        log.info("Email envoyé avec succès à : {}", email);
    }

    // ================== VERIFY CODE ==================
    @Transactional
    public boolean verifyCode(String email, String code) {
        log.info("Vérification du code pour : {}", email);

        // Validation des paramètres
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }

        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Le code ne peut pas être vide");
        }

        // Recherche de la vérification
        EmailVerification verification = verificationRepository
                .findByEmailAndCodeAndVerifiedFalse(email, code)
                .orElseThrow(() -> {
                    log.warn("Code invalide ou déjà utilisé pour : {}", email);
                    return new IllegalArgumentException("Code invalide ou déjà utilisé");
                });

        // Vérification de l'expiration
        if (verification.getExpiryDate().isBefore(LocalDateTime.now())) {
            log.warn("Code expiré pour : {}", email);
            verificationRepository.delete(verification);
            throw new IllegalArgumentException("Code expiré. Veuillez demander un nouveau code");
        }

        // Marquer comme vérifié
        verification.setVerified(true);
        verificationRepository.save(verification);
        log.info("Code vérifié avec succès pour : {}", email);

        return true;
    }

    // ================== CHECK IF EMAIL IS VERIFIED ==================
    public boolean isEmailVerified(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        return verificationRepository
                .findByEmailAndVerifiedTrue(email)
                .isPresent();
    }

    // ================== DELETE VERIFICATION AFTER REGISTRATION ==================
    @Transactional
    public void deleteVerification(String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        verificationRepository.findByEmail(email)
                .ifPresent(verification -> {
                    verificationRepository.delete(verification);
                    log.info("Vérification supprimée pour : {}", email);
                });
    }

    // ================== CLEAN EXPIRED VERIFICATIONS ==================
    @Transactional
    public void cleanExpiredVerifications() {
        log.info("Nettoyage des vérifications expirées");
        // Cette méthode peut être appelée par un scheduled task
        verificationRepository.deleteByExpiryDateBeforeAndVerifiedFalse(LocalDateTime.now());
    }
}