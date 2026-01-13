package com.app.hospitalService.Controller;

import com.app.hospitalService.Entity.Hospital;
import com.app.hospitalService.Service.HospitalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalController {
    
    private final HospitalService service;

    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospital(@PathVariable Long id) {
        log.info("Requête GET /hospital/{} - Récupération de l'hôpital", id);
        Hospital hospital = service.showHospital(id);
        return ResponseEntity.ok(hospital);
    }

    @GetMapping
    public ResponseEntity<List<Hospital>> getHospitals() {
        log.info("Requête GET /hospital - Récupération de tous les hôpitaux");
        List<Hospital> hospitals = service.showHospitals();
        return ResponseEntity.ok(hospitals);
    }

    @PostMapping
    public ResponseEntity<Hospital> createHospital(@RequestBody Hospital hospital) {
        log.info("Requête POST /hospital - Création d'un nouvel hôpital");
        Hospital createdHospital = service.createHospital(hospital);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHospital);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        log.info("Requête DELETE /hospital/{}/delete - Suppression de l'hôpital", id);
        service.removeHospital(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @RequestBody Hospital hospital) {
        log.info("Requête PUT /hospital/{}/update - Mise à jour de l'hôpital", id);
        Hospital updatedHospital = service.updateHospital(id, hospital);
        return ResponseEntity.ok(updatedHospital);
    }
}