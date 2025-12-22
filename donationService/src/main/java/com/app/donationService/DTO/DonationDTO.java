package com.app.donationService.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonationDTO{

    private Long id;
    private Long userId;
    private Long annonceId;
    private LocalDateTime applicationDate;
    private Boolean confirmed;

    @JsonProperty("user")
    private userInfo User;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class userInfo{
        private Long id;
        private Long villeId;
        private String username;
        private String email;
        private String address;
        private String role;
        private String bloodType;
        private Float score;
        private Integer phoneNumber;
        private String sex;
        private Integer hospitalId;
    }

    @JsonProperty("annonce")
    private annonceInfo Annonce;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class annonceInfo{
        private Long id;
        private String description;
        private String bloodType;
        private Integer quantity;
        private Date requestDate;
        private Integer hospitalId;
    }



}
