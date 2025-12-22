package com.app.donationService.Mapper;

import com.app.donationService.DTO.DonationDTO;
import com.app.donationService.Entity.Donation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DonationMapper {
    DonationDTO toDto(Donation donation);
    Donation toEntity(DonationDTO donationDTO);
}
