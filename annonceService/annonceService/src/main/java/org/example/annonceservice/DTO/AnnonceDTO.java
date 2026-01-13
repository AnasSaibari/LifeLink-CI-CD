package org.example.annonceservice.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.annonceservice.Entity.Annonce;
import org.example.annonceservice.Entity.UrgentLevel;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDTO {
    private Integer id;
    private String description;
    private String bloodType;
    private UrgentLevel urgentLevel;
    private Integer quantity;
    private String status;
    private Date requestDate;
    private Date deadline;

    private Long userId;
    private Long locationId;
    private Integer hospitalId;

    @JsonProperty("location")
    private LocationInfo location;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LocationInfo {
        private Long id;
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
        private Float longitude;
        private Float latitude;
    }


    @JsonProperty("user")
    private UserInfo User;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
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

}

