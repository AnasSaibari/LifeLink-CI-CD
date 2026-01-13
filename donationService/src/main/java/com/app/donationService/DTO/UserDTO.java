package com.app.donationService.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO {
    private Long id;
    private Long villeId;
    private String username;
    private String email;
    private String address;
    private String password;
    private Float score;
    private String role;
    private String sex;
    private String bloodType;
    private Integer phoneNumber;
    private Integer hospitalId;
}

