package com.app.notificationService.Service;

import com.app.notificationService.DTO.AnnonceDTO;
import com.app.notificationService.DTO.UserDTO;
import com.app.notificationService.FeignClient.HospitalClient;
import com.app.notificationService.FeignClient.LocationClient;
import com.app.notificationService.FeignClient.UserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final UserClient userClient;
    private final HospitalClient hospitalClient;
    private final LocationClient locationClient;

    public void notifyUsers(AnnonceDTO annonce) {

        // 1️⃣ Compléter hospital si null
        if (annonce.getHospital() == null && annonce.getHospitalId() != null) {
            var hDto = hospitalClient.getHospitalById(annonce.getHospitalId());
            if (hDto != null) {
                annonce.setHospital(new AnnonceDTO.HospitalInfo(
                        hDto.getId(),
                        hDto.getHospital_nom(),
                        hDto.getHospital_num()
                ));
            }
        }

        // 2️⃣ Compléter location si null
        if (annonce.getLocation() == null && annonce.getLocationId() != null) {
            var lDto = locationClient.getLocationById(annonce.getLocationId());
            if (lDto != null) {
                annonce.setLocation(new AnnonceDTO.LocationInfo(
                        lDto.getId(),
                        lDto.getVille()
                ));
            }
        }

        // 3️⃣ Récupérer tous les utilisateurs avec rôle
        List<UserDTO> users = userClient.getAllUsersWithRole();

        // 4️⃣ Envoyer l'email à chaque utilisateur
        for (UserDTO user : users) {
            emailService.sendEmail(
                    user.getEmail(),
                    "🚨 Demande urgente de sang",
                    buildMessage(annonce)
            );
        }
    }

    private String buildMessage(AnnonceDTO annonce) {
        String hospitalName = annonce.getHospital() != null ? annonce.getHospital().getHospital_nom() : "N/A";
        String ville = annonce.getLocation() != null ? annonce.getLocation().getVille() : "N/A";

        return """
            🚨 DEMANDE DE DON DE SANG 🚨
            
            🏥 Hôpital : %s
            🩸 Groupe sanguin : %s
            ⚠️ Urgence : %s
            ⏳ Deadline : %s
            📍 Ville : %s
            """.formatted(
                hospitalName,
                annonce.getBloodType(),
                annonce.getUrgentLevel(),
                annonce.getDeadline(),
                ville
        );
    }
}
