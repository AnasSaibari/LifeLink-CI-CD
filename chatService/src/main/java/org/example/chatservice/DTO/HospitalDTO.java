package org.example.chatservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalDTO {
    private Long id;
    private String hospital_nom;
    private String hospital_num;
}

