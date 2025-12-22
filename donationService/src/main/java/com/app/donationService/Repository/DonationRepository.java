package com.app.donationService.Repository;

import com.app.donationService.DTO.DonationDTO;
import com.app.donationService.Entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {

    List<Donation> findByUserId(Long userId);

    // Récupère toutes les donations confirmées
    List<Donation> findByConfirmedTrue();

    // Compte les donations confirmées pour un utilisateur
    long countByUserIdAndConfirmedTrue(Long userId);

    // Compte les donations confirmées pour une annonce
    long countByAnnonceIdAndConfirmedTrue(Long annonceId);
}