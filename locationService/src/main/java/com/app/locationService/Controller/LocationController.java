package com.app.locationService.Controller;

import com.app.locationService.DTO.LocationDTO;
import com.app.locationService.Service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationService service;

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> afficherLocation(@PathVariable Long id) {
        log.info("Requête GET /location/{} - Récupération de la location", id);
        LocationDTO location = service.getLocation(id);
        return ResponseEntity.ok(location);
    }

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        log.info("Requête GET /location - Récupération de toutes les locations");
        List<LocationDTO> locations = service.getAllLocations();
        return ResponseEntity.ok(locations);
    }
}
