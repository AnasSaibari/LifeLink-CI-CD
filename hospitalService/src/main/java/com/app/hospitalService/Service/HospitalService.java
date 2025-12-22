package com.app.hospitalService.Service;

import com.app.hospitalService.Entity.Hospital;
import com.app.hospitalService.Repository.HospitalRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class HospitalService {
    
    private final HospitalRepository repository;

    public HospitalService(HospitalRepository repository) {
        this.repository = repository;
    }

    public Hospital showHospital(Long id) {
        log.info("Récupération de l'hôpital avec l'ID: {}", id);
        
        try {
            Hospital hospital = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Hôpital non trouvé avec l'ID: {}", id);
                        return new EntityNotFoundException("Hospital not found with id: " + id);
                    });
            
            log.info("Hôpital récupéré avec succès - ID: {}, Nom: {}", hospital.getId(), hospital.getHospital_nom());
            return hospital;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'hôpital ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public List<Hospital> showHospitals() {
        log.info("Récupération de tous les hôpitaux");
        
        try {
            List<Hospital> hospitals = repository.findAll();
            log.info("Récupération réussie de {} hôpitaux", hospitals.size());
            return hospitals;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de tous les hôpitaux: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Hospital createHospital(Hospital hospital) {
        log.info("Création d'un nouvel hôpital: {}", hospital.getHospital_nom());
        
        try {
            Hospital savedHospital = repository.save(hospital);
            log.info("Hôpital créé avec succès - ID: {}, Nom: {}", savedHospital.getId(), savedHospital.getHospital_nom());
            return savedHospital;
        } catch (Exception e) {
            log.error("Erreur lors de la création de l'hôpital {}: {}", hospital.getHospital_nom(), e.getMessage(), e);
            throw e;
        }
    }

    public void removeHospital(Long id) {
        log.info("Suppression de l'hôpital avec l'ID: {}", id);
        
        try {
            Hospital hospital = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Tentative de suppression d'un hôpital inexistant - ID: {}", id);
                        return new EntityNotFoundException("Hospital not found with id: " + id);
                    });

            repository.delete(hospital);
            log.info("Hôpital supprimé avec succès - ID: {}, Nom: {}", hospital.getId(), hospital.getHospital_nom());
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'hôpital ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public Hospital updateHospital(Long id, Hospital hospital) {
        log.info("Mise à jour de l'hôpital avec l'ID: {}", id);
        
        try {
            Hospital existingHospital = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Tentative de mise à jour d'un hôpital inexistant - ID: {}", id);
                        return new EntityNotFoundException("Hospital not found with id: " + id);
                    });

            log.debug("Hôpital existant trouvé: {} ({})", existingHospital.getHospital_nom(), existingHospital.getHospital_num());

            // Mise à jour du nom
            if (hospital.getHospital_nom() != null && !hospital.getHospital_nom().isEmpty()) {
                log.debug("Mise à jour du nom: {} -> {}", existingHospital.getHospital_nom(), hospital.getHospital_nom());
                existingHospital.setHospital_nom(hospital.getHospital_nom());
            }

            // Mise à jour du numéro
            if (hospital.getHospital_num() != null && !hospital.getHospital_num().isEmpty()) {
                log.debug("Mise à jour du numéro: {} -> {}", existingHospital.getHospital_num(), hospital.getHospital_num());
                existingHospital.setHospital_num(hospital.getHospital_num());
            }

            Hospital savedHospital = repository.save(existingHospital);
            log.info("Hôpital mis à jour avec succès - ID: {}, Nom: {}", savedHospital.getId(), savedHospital.getHospital_nom());
            return savedHospital;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'hôpital ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}
