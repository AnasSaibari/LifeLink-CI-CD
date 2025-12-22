package com.app.donationService.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "donation")
@NoArgsConstructor
@AllArgsConstructor
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "annonce_id")
    private Long annonceId;
    private LocalDateTime applicationDate = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean confirmed =  false;

}
