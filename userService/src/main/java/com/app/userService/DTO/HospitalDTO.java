package com.app.userService.DTO;

import lombok.Data;

@Data
public class HospitalDTO {
    private Long id;
    private String hospital_nom;
    private String hospital_num;
    private Float longitude;
    private Float latitude;
}
