package com.app.donationService.Controller;

import com.app.donationService.DTO.DonationDTO;
import com.app.donationService.Service.DonationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    /* =====================================================
       CREATE Donation
       Body : userId + annonceId seulement
       ===================================================== */
    @PostMapping
    public ResponseEntity<DonationDTO> createDonation(
            @RequestBody DonationDTO donationDTO) {

        log.info("Création d'une donation (userId={}, annonceId={})",
                donationDTO.getUserId(), donationDTO.getAnnonceId());

        DonationDTO createdDonation = donationService.createDonation(donationDTO);
        return ResponseEntity.ok(createdDonation);
    }

    /* =====================================================
       GET all Donations
       ===================================================== */
    @GetMapping
    public ResponseEntity<List<DonationDTO>> getAllDonations() {

        List<DonationDTO> donations = donationService.getDonations();
        return ResponseEntity.ok(donations);
    }

    /* =====================================================
       GET Donation by ID
       ===================================================== */
    @GetMapping("/{id}")
    public ResponseEntity<DonationDTO> getDonationById(@PathVariable Long id) {

        DonationDTO donation = donationService.getDonationById(id);
        return ResponseEntity.ok(donation);
    }

    /* =====================================================
       GET Donation by User ID
       ===================================================== */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DonationDTO>> getDonationsByUserId(
            @PathVariable Long userId) {

        List<DonationDTO> donations = donationService.getDonationsByUserId(userId);
        return ResponseEntity.ok(donations);
    }

    /* =====================================================
       GET All Donations by Annoce.HospitalId
       ===================================================== */
    @GetMapping("/hospital/{hospitalId}")
    public ResponseEntity<List<DonationDTO>> getDonationsByHospitalId(@PathVariable Integer hospitalId) {
        List<DonationDTO> donations = donationService.getDonationsByHospitalId(hospitalId);
        return ResponseEntity.ok(donations);
    }

    /* =====================================================
       DELETE supprimer Donation by ID
       ===================================================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<DonationDTO> deleteDonationById(@PathVariable Long id) {
        donationService.deleteDonationById(id);
        return ResponseEntity.ok(new DonationDTO());
    }



    /* =====================================================
       GET Confirmed Donation
       ===================================================== */
    @GetMapping("/confirmed")
    public ResponseEntity<List<DonationDTO>> getAllConfirmedDonations() {
        List<DonationDTO> donations = donationService.getAllConfirmedDonations();
        return ResponseEntity.ok(donations);
    }

    /* =====================================================
       CONFIRM Donation
       ===================================================== */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<DonationDTO> confirmDonation(
            @PathVariable Long id) {

        DonationDTO donation = donationService.confirmDonation(id);
        return ResponseEntity.ok(donation);
    }

    // Nombre total de donations confirmées pour un user
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Long> getConfirmedCountByUser(@PathVariable Long userId) {
        long count = donationService.countConfirmedDonationsByUser(userId);
        return ResponseEntity.ok(count);
    }

    // Nombre total de donations confirmées pour une annonce
    @GetMapping("/stats/annonce/{annonceId}")
    public ResponseEntity<Long> getConfirmedCountByAnnonce(@PathVariable Long annonceId) {
        long count = donationService.countConfirmedDonationsByAnnonce(annonceId);
        return ResponseEntity.ok(count);
    }

    // Nombre total de donations confirmées pour un hospital
    @GetMapping("/stats/hospital/annonce/{hospitalId}")
    public ResponseEntity<Long> getConfirmedCountByAnnonceHospital(
            @PathVariable Long hospitalId) {
        long count = donationService.countConfirmedDonationsByAnnonceHospital(hospitalId);
        return ResponseEntity.ok(count);
    }


}
