package com.app.donationService.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDTO {
    private Integer id;
    @NotBlank(message = "La description est obligatoire")
    private String description;
    @NotBlank(message = "Le groupe sanguin est obligatoire")
    private String bloodType;
    @Positive(message = "La quantité doit être positive")
    private Integer quantity;
    private Date requestDate;
    private Integer hospitalId;

}

