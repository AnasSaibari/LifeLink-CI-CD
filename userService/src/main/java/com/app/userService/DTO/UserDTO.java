package com.app.userService.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String address;
    private String role;
    private String bloodType;
    private Float score;
    private String email;
    private Integer phoneNumber;
    private String sex;

    private Boolean verified;



    private Integer hospitalId;

    private Long villeId;


    @JsonProperty("location")
    private LocationInfo location;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationInfo {
        private Integer id;
        private String ville;
    }


    @JsonProperty("hospital")
    private HospitalInfo hospital;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HospitalInfo {
        private Long id;
        private String hospital_nom;
        private String hospital_num;
    }

}
