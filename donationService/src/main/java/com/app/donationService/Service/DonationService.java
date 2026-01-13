package com.app.donationService.Service;

import com.app.donationService.DTO.AnnonceDTO;
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
   Vérifie : groupe sanguin + pas de donation en double
   ===================================================== */
    public DonationDTO createDonation(DonationDTO donationDTO) {

        // 1. Vérifier si l'utilisateur a déjà fait une donation pour cette annonce
        boolean alreadyDonated = donationRepository.existsByUserIdAndAnnonceId(
                donationDTO.getUserId(),
                donationDTO.getAnnonceId()
        );

        if (alreadyDonated) {
            throw new RuntimeException(
                    String.format("Vous avez déjà postulé pour cette annonce (Annonce ID: %d)",
                            donationDTO.getAnnonceId())
            );
        }

        // 2. Enrichir pour récupérer les infos complètes
        Donation tempDonation = donationMapper.toEntity(donationDTO);
        DonationDTO enrichedDTO = enrichDonation(tempDonation);

        // 3. Vérifier que les données sont présentes
        if (enrichedDTO.getUser() == null || enrichedDTO.getUser().getBloodType() == null) {
            throw new RuntimeException("Impossible de récupérer les informations de l'utilisateur");
        }

        if (enrichedDTO.getAnnonce() == null || enrichedDTO.getAnnonce().getBloodType() == null) {
            throw new RuntimeException("Impossible de récupérer les informations de l'annonce");
        }

        // 4. Vérifier que le groupe sanguin correspond exactement
        String userBloodType = enrichedDTO.getUser().getBloodType().trim().toUpperCase();
        String annonceBloodType = enrichedDTO.getAnnonce().getBloodType().trim().toUpperCase();

        if (!userBloodType.equals(annonceBloodType)) {
            throw new RuntimeException(
                    String.format("Groupe sanguin incompatible : Vous êtes %s, l'annonce demande %s",
                            userBloodType, annonceBloodType)
            );
        }

        // 5. Créer la donation
        donationDTO.setApplicationDate(LocalDateTime.now());
        donationDTO.setConfirmed(false);

        Donation donation = donationMapper.toEntity(donationDTO);
        Donation savedDonation = donationRepository.save(donation);

        log.info("Donation créée avec succès (id={}) - User: {}, Annonce: {}, Type sanguin: {}",
                savedDonation.getId(),
                donationDTO.getUserId(),
                donationDTO.getAnnonceId(),
                userBloodType);

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
       GET All Donations by Annonce.HospitalId
       ===================================================== */
    public List<DonationDTO> getDonationsByHospitalId(Integer hospitalId) {
        List<Donation> donations = donationRepository.findAll();

        // enrichir et filtrer
        List<DonationDTO> result = donations.stream()
                .map(this::enrichDonation)
                .filter(dto ->
                        dto.getAnnonce() != null
                                && dto.getAnnonce().getHospitalId() != null
                                && dto.getAnnonce().getHospitalId().equals(hospitalId)
                )
                .toList();

        if (result.isEmpty()) {
            throw new RuntimeException(
                    "Aucune donation trouvée pour l'hôpital ID = " + hospitalId
            );
        }

        return result;
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
       DELETE supprimer Donation by ID
       ===================================================== */
    public void deleteDonationById(Long id) {
        donationRepository.deleteById(id);
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

        // -------- USER MS --------
        try {
            DonationDTO.userInfo user =
                    userClient.getUserById(dto.getUserId());
            dto.setUser(user);
        } catch (Exception e) {
            log.warn("Utilisateur {} introuvable", dto.getUserId());
        }

        // -------- ANNONCE MS --------
        try {
            // Feign retourne AnnonceDTO (du AnnonceService)
            AnnonceDTO annonceDTO =
                    annonceClient.getAnnonceById(dto.getAnnonceId());

            // Mapping vers DonationDTO.annonceInfo
            DonationDTO.annonceInfo annonceInfo =
                    new DonationDTO.annonceInfo();

            annonceInfo.setId(Long.valueOf(annonceDTO.getId()));
            annonceInfo.setDescription(annonceDTO.getDescription());
            annonceInfo.setBloodType(annonceDTO.getBloodType());
            annonceInfo.setQuantity(annonceDTO.getQuantity());
            annonceInfo.setRequestDate(annonceDTO.getRequestDate());
            annonceInfo.setHospitalId(annonceDTO.getHospitalId());

            // ⭐ MAPPING CORRECT DE L’HÔPITAL ⭐
            if (annonceDTO.getHospital() != null) {
                DonationDTO.annonceInfo.HospitalInfo hospital =
                        new DonationDTO.annonceInfo.HospitalInfo();

                hospital.setId(annonceDTO.getHospital().getId());
                hospital.setHospital_nom(
                        annonceDTO.getHospital().getHospital_nom()
                );
                hospital.setHospital_num(
                        annonceDTO.getHospital().getHospital_num()
                );
                hospital.setLatitude(
                        annonceDTO.getHospital().getLatitude()
                );
                hospital.setLongitude(
                        annonceDTO.getHospital().getLongitude()
                );

                annonceInfo.setHospital(hospital);
            }

            dto.setAnnonce(annonceInfo);

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
