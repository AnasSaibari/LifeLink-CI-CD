package com.app.userService.Entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String address;
    private String password;
    private Float score;
    private String role;
    private String sex;
    private String email;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "ville_id")
    private Long villeId;

    @Column(name = "phone_number")
    private Integer phoneNumber;

    @Column(name = "hospital_id")
    private Integer hospitalId;

}

