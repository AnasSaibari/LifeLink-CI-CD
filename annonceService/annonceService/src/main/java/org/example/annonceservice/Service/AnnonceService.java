package org.example.annonceservice.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.annonceservice.DTO.AnnonceDTO;
import org.example.annonceservice.DTO.LocationDTO;
import org.example.annonceservice.DTO.HospitalDTO;
import org.example.annonceservice.DTO.UserDTO;
import org.example.annonceservice.Entity.Annonce;
import org.example.annonceservice.FeignClient.LocationClient;
import org.example.annonceservice.FeignClient.UserClient;
import org.example.annonceservice.FeignClient.HospitalClient;
import org.example.annonceservice.Mapper.AnnonceMapper;
import org.example.annonceservice.Producer.AnnonceEventProducer;
import org.example.annonceservice.Repository.AnnonceRepository;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service

public class AnnonceService {

    private final AnnonceRepository repository;
    private final AnnonceMapper mapper;
    private final UserClient userClient;
    private final LocationClient locationClient;
    private final HospitalClient hospitalClient;
    private final AnnonceEventProducer eventProducer;

    public AnnonceService(AnnonceRepository repository, AnnonceMapper mapper, UserClient userClient, LocationClient locationClient, HospitalClient hospitalClient, AnnonceEventProducer eventProducer) {
        this.repository = repository;
        this.userClient = userClient;
        this.locationClient = locationClient;
        this.hospitalClient = hospitalClient;
        this.mapper = mapper;
        this.eventProducer = eventProducer;
    }

    public List<AnnonceDTO> getAll() {
        log.info("Récupération de toutes les annonces");
        List<Annonce> annonces = repository.findAll();
        log.info("Récupération réussie de {} annonces", annonces.size());
        List<AnnonceDTO> annonceDTOS = new ArrayList<>();
        for (Annonce annonce : annonces) {
            AnnonceDTO annonceDTO = mapper.toDto(annonce);
            // Récupération de l'utilisateur
            if (annonce.getUserId() != null) {
                try {
                    log.debug("Récupération de l'utilisateur pour l'annonce {} (UserId: {})", annonce.getId(), annonce.getUserId());
                    UserDTO userDTO = userClient.getUserById(annonce.getUserId());
                    annonceDTO.setUser(new AnnonceDTO.UserInfo(
                            userDTO.getId(),
                            userDTO.getVilleId(),
                            userDTO.getUsername(),
                            userDTO.getEmail(),
                            userDTO.getAddress(),
                            userDTO.getRole(),
                            userDTO.getBloodType(),
                            userDTO.getScore(),
                            userDTO.getPhoneNumber(),
                            userDTO.getSex(),
                            userDTO.getHospitalId()


                    ));
                    log.debug("Utilisateur récupéré: {}", userDTO.getId());
                } catch (Exception e) {
                    log.warn("Échec de récupération de l'utilisateur pour l'annonce {}: {}", annonce.getId(), e.getMessage());
                }
            }

            // Récupération de la localisation
            if (annonce.getLocationId() != null) {
                try {
                    log.debug("Récupération de la localisation pour l'annonce {} (LocationId: {})", annonce.getId(), annonce.getLocationId());
                    LocationDTO locationDTO = locationClient.getLocationById(annonce.getLocationId());
                    annonceDTO.setLocation(new AnnonceDTO.LocationInfo(
                            locationDTO.getId(),
                            locationDTO.getVille()
                    ));
                    log.debug("Localisation récupérée: {}", locationDTO.getId());
                } catch (Exception e) {
                    log.warn("Échec de récupération de la localisation pour l'annonce {}: {}", annonce.getId(), e.getMessage());
                }
            }
            // Récupération de Hospital
            if (annonce.getHospitalId() != null) {
                try {
                    HospitalDTO hospitalDTO =
                            hospitalClient.getHospitalById(annonce.getHospitalId());

                    annonceDTO.setHospital(
                            new AnnonceDTO.HospitalInfo(
                                    hospitalDTO.getId(),
                                    hospitalDTO.getHospital_nom(),
                                    hospitalDTO.getHospital_num(),
                                    hospitalDTO.getLongitude(),
                                    hospitalDTO.getLatitude()
                            )
                    );
                } catch (Exception e) {
                    log.warn("Impossible de récupérer l'hôpital {}", annonce.getHospitalId());
                }
            }


            annonceDTOS.add(annonceDTO);
            log.debug("Récuperation avec succès pour toutes les annonces");
        }
        return annonceDTOS;

    }

    public AnnonceDTO getById(Integer id) {
        log.info("Récupération de l'annonce ID {}", id);
        Annonce annonce = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce non trouvée avec id: " + id));

        AnnonceDTO annonceDTO = mapper.toDto(annonce);

        // Récupération de l'utilisateur
        if (annonce.getUserId() != null) {
            try {
                log.debug("Récupération de l'utilisateur pour l'annonce {} (UserId: {})", id, annonce.getUserId());
                UserDTO userDTO = userClient.getUserById(annonce.getUserId());
                annonceDTO.setUser(new AnnonceDTO.UserInfo(
                        userDTO.getId(),
                        userDTO.getVilleId(),
                        userDTO.getUsername(),
                        userDTO.getEmail(),
                        userDTO.getAddress(),
                        userDTO.getRole(),
                        userDTO.getBloodType(),
                        userDTO.getScore(),
                        userDTO.getPhoneNumber(),
                        userDTO.getSex(),
                        userDTO.getHospitalId()
                ));
                log.debug("Utilisateur récupéré: {}", userDTO.getId());
            } catch (Exception e) {
                log.warn("Échec de récupération de l'utilisateur pour l'annonce {}: {}", id, e.getMessage());
            }
        }

        // Récupération de la localisation
        if (annonce.getLocationId() != null) {
            try {
                log.debug("Récupération de la localisation pour l'annonce {} (LocationId: {})", id, annonce.getLocationId());
                LocationDTO locationDTO = locationClient.getLocationById(annonce.getLocationId());
                annonceDTO.setLocation(new AnnonceDTO.LocationInfo(
                        locationDTO.getId(),
                        locationDTO.getVille()
                ));
                log.debug("Localisation récupérée: {}", locationDTO.getId());
            } catch (Exception e) {
                log.warn("Échec de récupération de la localisation pour l'annonce {}: {}", id, e.getMessage());
            }
        }
        // Récupération de Hospital
        if (annonce.getHospitalId() != null) {
            try {
                HospitalDTO hospitalDTO =
                        hospitalClient.getHospitalById(annonce.getHospitalId());

                annonceDTO.setHospital(
                        new AnnonceDTO.HospitalInfo(
                                hospitalDTO.getId(),
                                hospitalDTO.getHospital_nom(),
                                hospitalDTO.getHospital_num(),
                                hospitalDTO.getLongitude(),
                                hospitalDTO.getLatitude()
                        )
                );
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'hôpital {}", annonce.getHospitalId());
            }
        }

        return annonceDTO;
    }

    public List<AnnonceDTO> getAnnonceByHospitalId(Long hospitalId) {
        log.info("Récupération de toutes les annonces pour l'hospital {}", hospitalId);
        List<Annonce> annonces = repository.findByHospitalId(hospitalId);
        log.info("Récupération réussie de {} annonces", annonces.size());
        List<AnnonceDTO> annonceDTOS = new ArrayList<>();
        for (Annonce annonce : annonces) {
            AnnonceDTO annonceDTO = mapper.toDto(annonce);
            // Récupération de l'utilisateur
            if (annonce.getUserId() != null) {
                try {
                    log.debug("Récupération de l'utilisateur pour l'annonce {} (UserId: {})", annonce.getId(), annonce.getUserId());
                    UserDTO userDTO = userClient.getUserById(annonce.getUserId());
                    annonceDTO.setUser(new AnnonceDTO.UserInfo(
                            userDTO.getId(),
                            userDTO.getVilleId(),
                            userDTO.getUsername(),
                            userDTO.getEmail(),
                            userDTO.getAddress(),
                            userDTO.getRole(),
                            userDTO.getBloodType(),
                            userDTO.getScore(),
                            userDTO.getPhoneNumber(),
                            userDTO.getSex(),
                            userDTO.getHospitalId()


                    ));
                    log.debug("Utilisateur récupéré: {}", userDTO.getId());
                } catch (Exception e) {
                    log.warn("Échec de récupération de l'utilisateur pour l'annonce {}: {}", annonce.getId(), e.getMessage());
                }
            }

            // Récupération de la localisation
            if (annonce.getLocationId() != null) {
                try {
                    log.debug("Récupération de la localisation pour l'annonce {} (LocationId: {})", annonce.getId(), annonce.getLocationId());
                    LocationDTO locationDTO = locationClient.getLocationById(annonce.getLocationId());
                    annonceDTO.setLocation(new AnnonceDTO.LocationInfo(
                            locationDTO.getId(),
                            locationDTO.getVille()
                    ));
                    log.debug("Localisation récupérée: {}", locationDTO.getId());
                } catch (Exception e) {
                    log.warn("Échec de récupération de la localisation pour l'annonce {}: {}", annonce.getId(), e.getMessage());
                }
            }
            // Récupération de Hospital
            if (annonce.getHospitalId() != null) {
                try {
                    HospitalDTO hospitalDTO =
                            hospitalClient.getHospitalById(annonce.getHospitalId());

                    annonceDTO.setHospital(
                            new AnnonceDTO.HospitalInfo(
                                    hospitalDTO.getId(),
                                    hospitalDTO.getHospital_nom(),
                                    hospitalDTO.getHospital_num(),
                                    hospitalDTO.getLongitude(),
                                    hospitalDTO.getLatitude()
                            )
                    );
                } catch (Exception e) {
                    log.warn("Impossible de récupérer l'hôpital {}", annonce.getHospitalId());
                }
            }


            annonceDTOS.add(annonceDTO);
            log.debug("Récuperation avec succès pour toutes les annonces");
        }
        return annonceDTOS;
    }

    public AnnonceDTO create(AnnonceDTO dto) {
        log.info("Création d'une nouvelle annonce: {}", dto.getBloodType());

        validateReferences(dto);

        Annonce annonce = mapper.toEntity(dto);

        if (annonce.getRequestDate() == null) {
            annonce.setRequestDate(new Date());
        }
        if (annonce.getStatus() == null) {
            annonce.setStatus(Annonce.AnnonceStatus.BROUILLON);
        }

        Annonce saved = repository.save(annonce);
        eventProducer.publishAnnonceCreated(dto);
        log.info("Annonce créée avec succès - ID: {}", saved.getId());
        return mapper.toDto(saved);
    }

    @Transactional
    public AnnonceDTO update(Integer id, AnnonceDTO dto) {
        log.info("Mise à jour de l'annonce ID {}", id);
        Annonce existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce non trouvée avec id: " + id));

        validateReferences (dto);

        Annonce updated = mapper.updateAnnonceFromDto(dto, existing);
        updated.setId(existing.getId());

        if (updated.getRequestDate() == null) {
            updated.setRequestDate(existing.getRequestDate());
        }

        Annonce saved = repository.save(updated);
        log.info("Annonce mise à jour - ID: {}", saved.getId());
        return mapper.toDto(saved);
    }

    public Annonce delete(Integer id) {
        log.info("Suppression de l'annonce ID {}", id);
        Annonce existing = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce non trouvée avec id: " + id));
        repository.delete(existing);
        log.info("Annonce supprimée - ID: {}", existing.getId());
        return existing;
    }

    private void validateReferences(AnnonceDTO dto) {
        if (dto.getUserId() != null) {
            try {
                userClient.getUserById(dto.getUserId());
            } catch (Exception e) {
                log.warn("Utilisateur avec id {} introuvable", dto.getUserId());
                throw new IllegalArgumentException("Utilisateur avec id " + dto.getUserId() + " introuvable");
            }
        }

        if (dto.getLocationId() != null) {
            try {
                locationClient.getLocationById(dto.getLocationId());
            } catch (Exception e) {
                log.warn("Location avec id {} introuvable", dto.getLocationId());
                throw new IllegalArgumentException("Location avec id " + dto.getLocationId() + " introuvable");
            }
        }
    }
    // ========== MÉTHODES DE RECHERCHE/FILTRAGE ==========

    private AnnonceDTO enrichAnnonceDTO(Annonce annonce) {
        AnnonceDTO annonceDTO = mapper.toDto(annonce);

        // Récupération de l'utilisateur
        if (annonce.getUserId() != null) {
            try {
                UserDTO userDTO = userClient.getUserById(annonce.getUserId());
                annonceDTO.setUser(new AnnonceDTO.UserInfo(
                        userDTO.getId(),
                        userDTO.getVilleId(),
                        userDTO.getUsername(),
                        userDTO.getEmail(),
                        userDTO.getAddress(),
                        userDTO.getRole(),
                        userDTO.getBloodType(),
                        userDTO.getScore(),
                        userDTO.getPhoneNumber(),
                        userDTO.getSex(),
                        userDTO.getHospitalId()
                ));
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'utilisateur {}", annonce.getUserId());
            }
        }

        // Récupération de la location
        if (annonce.getLocationId() != null) {
            try {
                LocationDTO locationDTO = locationClient.getLocationById(annonce.getLocationId());
                annonceDTO.setLocation(new AnnonceDTO.LocationInfo(
                        locationDTO.getId(),
                        locationDTO.getVille()
                ));
            } catch (Exception e) {
                log.warn("Impossible de récupérer la location {}", annonce.getLocationId());
            }
        }
        // Récupération de Hospital
        if (annonce.getHospitalId() != null) {
            try {
                HospitalDTO hospitalDTO =
                        hospitalClient.getHospitalById(annonce.getHospitalId());

                annonceDTO.setHospital(
                        new AnnonceDTO.HospitalInfo(
                                hospitalDTO.getId(),
                                hospitalDTO.getHospital_nom(),
                                hospitalDTO.getHospital_num(),
                                hospitalDTO.getLongitude(),
                                hospitalDTO.getLatitude()
                        )
                );
            } catch (Exception e) {
                log.warn("Impossible de récupérer l'hôpital {}", annonce.getHospitalId());
            }
        }

        return annonceDTO;
    }


    public List<AnnonceDTO> searchByBloodType(String bloodType) {
        log.info("Recherche d'annonces par blood type: {}", bloodType);
        List<AnnonceDTO> annonces = repository.findByBloodTypeIgnoreCase(bloodType)
                .stream()
                .map(this::enrichAnnonceDTO)
                .collect(Collectors.toList());
        log.info("Trouvé {} annonces pour le blood type: {}", annonces.size(), bloodType);
        return annonces;
    }

    public List<AnnonceDTO> searchByStatus(String status) {
        log.info("Recherche d'annonces par status: {}", status);
        List<AnnonceDTO> annonces = repository.findByStatusIgnoreCase(status)
                .stream()
                .map(this::enrichAnnonceDTO)
                .collect(Collectors.toList());
        log.info("Trouvé {} annonces pour le status: {}", annonces.size(), status);
        return annonces;
    }

    public List<AnnonceDTO> searchByLocationId(Long locationId) {
        log.info("Recherche d'annonces par location ID: {}", locationId);
        List<AnnonceDTO> annonces = repository.findByLocationId(locationId)
                .stream()
                .map(this::enrichAnnonceDTO)
                .collect(Collectors.toList());
        log.info("Trouvé {} annonces pour la location ID: {}", annonces.size(), locationId);
        return annonces;
    }
}

