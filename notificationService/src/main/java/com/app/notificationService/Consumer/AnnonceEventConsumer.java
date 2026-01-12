package com.app.notificationService.Consumer;

import com.app.notificationService.DTO.AnnonceDTO;
import com.app.notificationService.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.example.annonceservice.Event.AnnonceCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnonceEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "annonce-created-topic",
            groupId = "notification-group"
    )
    public void listen(AnnonceCreatedEvent event) {
        if (event == null || event.getAnnonce() == null) return;

        // 1️⃣ Transformer l'AnnonceDTO reçu (du microservice annonce) en DTO local
        org.example.annonceservice.DTO.AnnonceDTO annonceFromEvent = event.getAnnonce();
        AnnonceDTO annonceLocal = mapToLocalDTO(annonceFromEvent);

        // 2️⃣ Appeler le service pour notifier les utilisateurs
        notificationService.notifyUsers(annonceLocal);
    }

    private AnnonceDTO mapToLocalDTO(org.example.annonceservice.DTO.AnnonceDTO a) {
        if (a == null) return null;

        // Mapper les champs principaux
        AnnonceDTO annonce = new AnnonceDTO();
        annonce.setId(a.getId());
        annonce.setDescription(a.getDescription());
        annonce.setBloodType(a.getBloodType());
        annonce.setUrgentLevel(a.getUrgentLevel());
        annonce.setQuantity(a.getQuantity());
        annonce.setStatus(a.getStatus());
        annonce.setRequestDate(a.getRequestDate());
        annonce.setDeadline(a.getDeadline());
        annonce.setUserId(a.getUserId());
        annonce.setHospitalId(a.getHospitalId());
        annonce.setLocationId(a.getLocationId());

        // Mapper hospital et location si déjà présents
        if (a.getHospital() != null) {
            annonce.setHospital(new AnnonceDTO.HospitalInfo(
                    a.getHospital().getId(),
                    a.getHospital().getHospital_nom(),
                    a.getHospital().getHospital_num()
            ));
        }

        if (a.getLocation() != null) {
            annonce.setLocation(new AnnonceDTO.LocationInfo(
                    a.getLocation().getId(),
                    a.getLocation().getVille()
            ));
        }

        return annonce;
    }
}
