package com.app.donationService.Repository;

import com.app.donationService.DTO.DonationDTO;
import com.app.donationService.Entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    //Récupère toutes les donations by userId
    List<Donation> findByUserId(Long userId);

    // Récupère toutes les donations confirmées
    List<Donation> findByConfirmedTrue();

    // Vérifier si une donation existe déjà pour cet utilisateur et cette annonce
    boolean existsByUserIdAndAnnonceId(Long userId, Long annonceId);

    // Compte les donations confirmées pour un utilisateur
    long countByUserIdAndConfirmedTrue(Long userId);

    // Compte les donations confirmées pour une annonce
    long countByAnnonceIdAndConfirmedTrue(Long annonceId);
}