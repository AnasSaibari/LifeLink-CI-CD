package com.app.donationService.Service;

import com.app.donationService.DTO.DonationDTO;
import com.app.donationService.Entity.Donation;
import com.app.donationService.FeignClient.AnnonceClient;
import com.app.donationService.FeignClient.UserClient;
import com.app.donationService.Mapper.DonationMapper;
import com.app.donationService.Repository.DonationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationMapper donationMapper;
    private final UserClient userClient;
    private final AnnonceClient annonceClient;

    public DonationService(DonationRepository donationRepository,
                           DonationMapper donationMapper,
                           UserClient userClient,
                           AnnonceClient annonceClient) {
        this.donationRepository = donationRepository;
        this.donationMapper = donationMapper;
        this.userClient = userClient;
        this.annonceClient = annonceClient;
    }

    /* =====================================================
       CREATE Donation
       Client envoie : userId + annonceId
       ===================================================== */
    public DonationDTO createDonation(DonationDTO donationDTO) {

        donationDTO.setApplicationDate(LocalDateTime.now());
        donationDTO.setConfirmed(false);

        Donation donation = donationMapper.toEntity(donationDTO);
        Donation savedDonation = donationRepository.save(donation);

        log.info("Donation créée avec succès (id={})", savedDonation.getId());

        return enrichDonation(savedDonation);
    }

    /* =====================================================
       GET all Donations (avec user + annonce)
       ===================================================== */
    public List<DonationDTO> getDonations() {

        return donationRepository.findAll()
                .stream()
                .map(this::enrichDonation)
                .toList();
    }

    /* =====================================================
       GET Donation by ID
       ===================================================== */
    public DonationDTO getDonationById(Long id) {

        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation non trouvée"));

        return enrichDonation(donation);
    }

    /* =====================================================
       GET Donation by User ID
       ===================================================== */
    public List<DonationDTO> getDonationsByUserId(Long userId) {
        List<Donation> donations = donationRepository.findByUserId(userId);

        if (donations.isEmpty()) {
            throw new RuntimeException("Aucune donation pour cet utilisateur");
        }

        return donations.stream()
                .map(this::enrichDonation)
                .toList();
    }


    /* =====================================================
       GET Confirmed Donation
       ===================================================== */
    public List<DonationDTO> getAllConfirmedDonations() {
        List<Donation> donations = donationRepository.findByConfirmedTrue();

        return donations.stream()
                .map(this::enrichDonation)
                .toList();
    }

    /* =====================================================
       CONFIRM Donation
       ===================================================== */
    public DonationDTO confirmDonation(Long donationId) {

        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation non trouvée"));

        donation.setConfirmed(true);

        Donation updatedDonation = donationRepository.save(donation);

        log.info("Donation {} confirmée", donationId);

        return enrichDonation(updatedDonation);
    }

    /* =====================================================
       Méthode centrale : enrichir DonationDTO
       ===================================================== */
    private DonationDTO enrichDonation(Donation donation) {

        DonationDTO dto = donationMapper.toDto(donation);

        // -------- User MS --------
        try {
            DonationDTO.userInfo user =
                    userClient.getUserById(dto.getUserId());
            dto.setUser(user);
        } catch (Exception e) {
            log.warn("Utilisateur {} introuvable", dto.getUserId());
        }

        // -------- Annonce MS --------
        try {
            DonationDTO.annonceInfo annonce =
                    annonceClient.getAnnonceById(dto.getAnnonceId());
            dto.setAnnonce(annonce);
        } catch (Exception e) {
            log.warn("Annonce {} introuvable", dto.getAnnonceId());
        }

        return dto;
    }

    public long countConfirmedDonationsByAnnonceHospital(Long hospitalId) {
        // 1️⃣ Récupérer toutes les donations confirmées
        List<Donation> donations = donationRepository.findByConfirmedTrue();

        // 2️⃣ Enrichir via Feign (user + annonce)
        List<DonationDTO> enrichedDonations = new ArrayList<>();
        for (Donation d : donations) {
            try {
                DonationDTO dto = enrichDonation(d);
                enrichedDonations.add(dto);
            } catch (Exception e) {
                // log et skip si Feign échoue
                System.out.println("Impossible d'enrichir donation " + d.getId());
            }
        }

        // 3️⃣ Filtrer celles dont l'annonce a le hospitalId demandé
        long count = enrichedDonations.stream()
                .filter(dto -> dto.getAnnonce() != null
                        && dto.getAnnonce().getHospitalId() != null
                        && dto.getAnnonce().getHospitalId().longValue() == hospitalId)
                .count();

        return count;

    }


    public long countConfirmedDonationsByUser(Long userId) {
        return donationRepository.countByUserIdAndConfirmedTrue(userId);
    }

    public long countConfirmedDonationsByAnnonce(Long annonceId) {
        return donationRepository.countByAnnonceIdAndConfirmedTrue(annonceId);
    }


}
