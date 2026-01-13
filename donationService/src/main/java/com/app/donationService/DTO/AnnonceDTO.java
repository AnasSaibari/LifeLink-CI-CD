package com.app.donationService.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String description;
    private String bloodType;
    private Integer quantity;
    private Date requestDate;
    private Integer hospitalId;

    @JsonProperty("hospital")
    private HospitalInfo hospital;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalInfo {
        private Long id;
        private String hospital_nom;
        private String hospital_num;
        private Float longitude;
        private Float latitude;
    }



}

