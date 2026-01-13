package com.app.donationService.FeignClient;

import com.app.donationService.DTO.AnnonceDTO;
import com.app.donationService.DTO.DonationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "annonce-service", url = "http://localhost:9094/api/annonces")
public interface AnnonceClient {

    @GetMapping("{id}")
    AnnonceDTO getAnnonceById(@PathVariable Long id);
}