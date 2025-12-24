package org.example.annonceservice.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.annonceservice.DTO.AnnonceDTO;
import org.example.annonceservice.Entity.Annonce;
import org.example.annonceservice.Service.AnnonceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/annonces")
@RequiredArgsConstructor
public class AnnonceController {

    private final AnnonceService service;

    @GetMapping
    public ResponseEntity<List<AnnonceDTO>> getAll() {
        log.info("Requête GET /api/annonces - liste des annonces");
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnnonceDTO> getById(@PathVariable Integer id) {
        log.info("Requête GET /api/annonces/{} - détail annonce", id);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/hospital/{id}")
    public ResponseEntity<List<AnnonceDTO>> getByHospitalId(@PathVariable Long id) {
        log.info("Requête GET /api/annonces - liste des annonces par hospital {}", id);
        return ResponseEntity.ok(service.getAnnonceByHospitalId(id));
    }

    @PostMapping
    public ResponseEntity<AnnonceDTO> create(@Valid @RequestBody AnnonceDTO annonce) {
        log.info("Requête POST /api/annonces - création annonce");
        AnnonceDTO created = service.create(annonce);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<AnnonceDTO> update(@PathVariable Integer id, @Valid @RequestBody AnnonceDTO dto) {
        log.info("Requête PUT /api/annonces/{}/update - mise à jour annonce", id);
        AnnonceDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Annonce> delete(@PathVariable Integer id) {
        log.info("Requête DELETE /api/annonces/{}/delete - suppression annonce", id);
        Annonce deleted = service.delete(id);
        return ResponseEntity.ok(deleted);
    }
    @GetMapping("/search/bloodType/{bloodType}")
    public List<AnnonceDTO> searchByBloodType(@PathVariable String bloodType) {
        return service.searchByBloodType(bloodType);
    }

    @GetMapping("/search/status/{status}")
    public List<AnnonceDTO> searchByStatus(@PathVariable String status) {
        return service.searchByStatus(status);
    }

    @GetMapping("/search/location/{locationId}")
    public List<AnnonceDTO> searchByLocation(@PathVariable Long locationId) {
        return service.searchByLocationId(locationId);
    }
}

