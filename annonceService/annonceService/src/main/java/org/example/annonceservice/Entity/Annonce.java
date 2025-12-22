package org.example.annonceservice.Entity;
import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_annonce")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Annonce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    @Column(name = "blood_type")
    private String bloodType;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgent_level")
    private UrgentLevel urgentLevel;

    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private AnnonceStatus status;

    @Column(name = "request_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name = "deadline")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deadline;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "location_id")
    private Long locationId;

    @Column(name = "hospital_id")
    private Long hospitalId;

    public enum AnnonceStatus {
        EN_COURS,
        CLÔTURÉ,
        EXPIRÉ,
        BROUILLON
    }
}
