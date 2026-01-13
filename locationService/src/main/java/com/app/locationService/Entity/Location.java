package com.app.locationService.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name="location")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ville;

}
