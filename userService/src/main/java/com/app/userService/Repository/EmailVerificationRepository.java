package com.app.userService.Repository;

import com.app.userService.Entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    // Trouver une vérification non vérifiée par email
    Optional<EmailVerification> findByEmailAndVerifiedFalse(String email);

    // Trouver une vérification avec email et code (non vérifiée)
    Optional<EmailVerification> findByEmailAndCodeAndVerifiedFalse(String email, String code);

    // Trouver une vérification vérifiée par email
    Optional<EmailVerification> findByEmailAndVerifiedTrue(String email);

    // Trouver n'importe quelle vérification par email
    Optional<EmailVerification> findByEmail(String email);

    // Supprimer les vérifications expirées et non vérifiées (pour le nettoyage)
    void deleteByExpiryDateBeforeAndVerifiedFalse(LocalDateTime date);
}