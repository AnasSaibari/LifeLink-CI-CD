package com.app.locationService.Service;

import com.app.locationService.DTO.LocationDTO;
import com.app.locationService.Entity.Location;
import com.app.locationService.Repository.LocationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LocationService {
    
    private final LocationRepository repository;

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public LocationDTO getLocation(Long id) {
        log.info("Récupération de la location avec l'ID: {}", id);
        
        try {
            Location location = repository.findById(Math.toIntExact(id))
                    .orElseThrow(() -> {
                        log.warn("Location non trouvée avec l'ID: {}", id);
                        return new EntityNotFoundException("Location not found with id: " + id);
                    });

            LocationDTO locationDTO = new LocationDTO(location.getId(), location.getVille());
            log.info("Location récupérée avec succès - ID: {}, Ville: {}", location.getId(), location.getVille());
            return locationDTO;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la location ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public List<LocationDTO> getAllLocations() {
        log.info("Récupération de toutes les locations");
        
        try {
            List<Location> locations = repository.findAll();
            log.debug("Nombre de locations trouvées: {}", locations.size());
            
            List<LocationDTO> locationDTOS = new ArrayList<>();
            for (Location location : locations) {
                locationDTOS.add(new LocationDTO(location.getId(), location.getVille()));
            }
            
            log.info("Récupération réussie de {} locations", locationDTOS.size());
            return locationDTOS;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de toutes les locations: {}", e.getMessage(), e);
            throw e;
        }
    }
}
