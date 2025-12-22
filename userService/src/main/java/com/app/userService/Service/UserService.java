package com.app.userService.Service;

import com.app.userService.DTO.HospitalDTO;
import com.app.userService.DTO.LocationDTO;
import com.app.userService.DTO.ReviewDTO;
import com.app.userService.DTO.UserDTO;
import com.app.userService.Entity.EmailVerification;
import com.app.userService.Entity.User;
import com.app.userService.FeignClient.HospitalClient;
import com.app.userService.FeignClient.LocationClient;
import com.app.userService.FeignClient.ReviewClient;
import com.app.userService.Mapper.UserMapper;
import com.app.userService.Repository.EmailVerificationRepository;
import com.app.userService.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j   // ✅ FIX LOGGER
@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final LocationClient locationClient;
    private final HospitalClient hospitalClient;
    private final ReviewClient reviewClient;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository repository,
                       UserMapper mapper,
                       LocationClient locationClient,
                       HospitalClient hospitalClient,
                       ReviewClient reviewClient,
                       PasswordEncoder passwordEncoder,
                       EmailVerificationRepository emailVerificationRepository,
                       EmailVerificationService emailVerificationService) {   // ✅ FIX ICI
        this.repository = repository;
        this.mapper = mapper;
        this.locationClient = locationClient;
        this.hospitalClient = hospitalClient;
        this.reviewClient = reviewClient;
        this.passwordEncoder = passwordEncoder;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailVerificationService = emailVerificationService;
    }

    public List<UserDTO> getAllUsers() {
        log.info("Récupération de tous les utilisateurs");
        
        try {
            List<User> users = repository.findAll();
            log.debug("Nombre d'utilisateurs trouvés: {}", users.size());
            
            List<UserDTO> userDTOS = new ArrayList<>();

            for (User user : users) {
                // Conversion User -> UserDTO
                UserDTO userDTO = mapper.toDto(user);

                // ✅ Vérification de location
                if (user.getVilleId() != null) {
                    try {
                        LocationDTO location = locationClient.getLocationById(user.getVilleId());
                        userDTO.setLocation(new UserDTO.LocationInfo(
                                Math.toIntExact(location.getId()),
                                location.getVille()
                        ));
                        log.debug("Location récupérée pour l'utilisateur {}: {}", user.getId(), location.getVille());
                    } catch (Exception e) {
                        log.warn("Échec de récupération de la location pour l'utilisateur {}: {}", user.getId(), e.getMessage());
                    }
                }

                // ✅ Vérification de l'hôpital
                if (user.getHospitalId() != null) {
                    try {
                        HospitalDTO hospital = hospitalClient.getHospitalById(
                                Long.valueOf(user.getHospitalId())
                        );
                        userDTO.setHospital(new UserDTO.HospitalInfo(
                                (long) Math.toIntExact(hospital.getId()),
                                hospital.getHospital_nom(),
                                hospital.getHospital_num()
                        ));
                        log.debug("Hôpital récupéré pour l'utilisateur {}: {}", user.getId(), hospital.getHospital_nom());
                    } catch (Exception e) {
                        log.warn("Échec de récupération de l'hôpital pour l'utilisateur {}: {}", user.getId(), e.getMessage());
                    }
                }

                userDTOS.add(userDTO);
            }

            log.info("Récupération réussie de {} utilisateurs", userDTOS.size());
            return userDTOS;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de tous les utilisateurs: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ---------------- REVIEWS ----------------

    public List<ReviewDTO> getAllReviews() {
        log.info("Récupération de toutes les reviews via reviewService");
        return reviewClient.getAllReviews();
    }

    public List<ReviewDTO> getReviewsByUser(Long userId) {
        log.info("Récupération des reviews pour l'utilisateur ID: {}", userId);
        return reviewClient.getReviewsByUser(userId);
    }

    public Double getAverageRatingByUser(Long userId) {
        log.info("Récupération de la moyenne des ratings pour l'utilisateur ID: {}", userId);
        return reviewClient.getAverageByUser(userId);
    }

    public Double getAverageRatingAllUsers() {
        log.info("Récupération de la moyenne des ratings pour tous les utilisateurs");
        return reviewClient.getAverageAll();
    }


    public UserDTO getUser(Integer id) {
        log.info("Récupération de l'utilisateur avec l'ID: {}", id);
        
        try {
            User user = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Utilisateur non trouvé avec l'ID: {}", id);
                        return new EntityNotFoundException("User not found with id: " + id);
                    });

            log.debug("Utilisateur trouvé: {} ({})", user.getUsername(), user.getEmail());
            UserDTO userDTO = mapper.toDto(user);

            //---------------- LOCATION -----------------
            if (user.getVilleId() != null) {
                try {
                    log.debug("Récupération de la location pour l'utilisateur {} (villeId: {})", id, user.getVilleId());
                    LocationDTO location = locationClient.getLocationById(user.getVilleId());
                    userDTO.setLocation(new UserDTO.LocationInfo(
                            Math.toIntExact(location.getId()),
                            location.getVille()
                    ));
                    log.debug("Location récupérée: {}", location.getVille());
                } catch (Exception e) {
                    log.warn("Échec de récupération de la location pour l'utilisateur {}: {}", id, e.getMessage());
                }
            }

            //---------------- HOSPITAL ----------------
            if (user.getHospitalId() != null) {
                try {
                    log.debug("Récupération de l'hôpital pour l'utilisateur {} (hospitalId: {})", id, user.getHospitalId());
                    HospitalDTO hospital = hospitalClient.getHospitalById(user.getHospitalId());
                    userDTO.setHospital(new UserDTO.HospitalInfo(
                            hospital.getId(),
                            hospital.getHospital_nom(),
                            hospital.getHospital_num()
                    ));
                    log.debug("Hôpital récupéré: {}", hospital.getHospital_nom());
                } catch (Exception e) {
                    log.warn("Échec de récupération de l'hôpital pour l'utilisateur {}: {}", id, e.getMessage());
                }
            }

            log.info("Récupération réussie de l'utilisateur ID: {}", id);
            return userDTO;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


    public User saveUser(User user) {
        log.info("Création d'un nouvel utilisateur: {}", user.getEmail() != null ? user.getEmail() : user.getUsername());
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé !");
        } else {


            try {
                // Encoder le mot de passe s'il n'est pas déjà encodé (format BCrypt commence par $2a$ ou $2b$)
                if (user.getPassword() != null && !user.getPassword().isEmpty()
                        && !user.getPassword().startsWith("$2a$") && !user.getPassword().startsWith("$2b$")) {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    log.info("Mot de passe encodé pour l'utilisateur: {}", user.getEmail() != null ? user.getEmail() : user.getUsername());
                }

                User savedUser = repository.save(user);
                log.info("Utilisateur créé avec succès - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
                return savedUser;
            } catch (Exception e) {
                log.error("Erreur lors de la création de l'utilisateur {}: {}",
                        user.getEmail() != null ? user.getEmail() : user.getUsername(), e.getMessage(), e);
                throw e;
            }
        }
    }


    public User deleteUserById(Integer id) {
        log.info("Suppression de l'utilisateur avec l'ID: {}", id);
        
        try {
            User user = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Tentative de suppression d'un utilisateur inexistant - ID: {}", id);
                        return new EntityNotFoundException("User not found with id: " + id);
                    });

            repository.delete(user);
            log.info("Utilisateur supprimé avec succès - ID: {}, Email: {}", user.getId(), user.getEmail());
            return user;
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression de l'utilisateur ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }



    @Transactional
    public UserDTO updateUser(Integer id, UserDTO userDTO) {
        log.info("Mise à jour de l'utilisateur avec l'ID: {}", id);
        
        try {
            User existingUser = repository.findById(id)
                    .orElseThrow(() -> {
                        log.warn("Tentative de mise à jour d'un utilisateur inexistant - ID: {}", id);
                        return new EntityNotFoundException("User not found with id: " + id);
                    });

            log.debug("Utilisateur existant trouvé: {} ({})", existingUser.getUsername(), existingUser.getEmail());
            User updatedUser = mapper.updateUserFromDto(userDTO, existingUser);

            // ✅ Validation de la ville
            if (userDTO.getVilleId() != null) {
                try {
                    log.debug("Validation de la ville avec l'ID: {}", userDTO.getVilleId());
                    LocationDTO location = locationClient.getLocationById(userDTO.getVilleId());
                    updatedUser.setVilleId(location.getId());
                    log.debug("Ville validée: {}", location.getVille());
                } catch (Exception e) {
                    log.error("Ville avec l'id {} n'existe pas: {}", userDTO.getVilleId(), e.getMessage());
                    throw new IllegalArgumentException(
                            "Ville avec l'id " + userDTO.getVilleId() + " n'existe pas"
                    );
                }
            }

            // ✅ Validation de l'hopital
            if (userDTO.getHospitalId() != null) {
                try {
                    log.debug("Validation de l'hôpital avec l'ID: {}", userDTO.getHospitalId());
                    HospitalDTO hospital = hospitalClient.getHospitalById(userDTO.getHospitalId());
                    updatedUser.setHospitalId(Math.toIntExact(hospital.getId()));
                    log.debug("Hôpital validé: {}", hospital.getHospital_nom());
                } catch (Exception e) {
                    log.error("Hôpital avec l'id {} n'existe pas: {}", userDTO.getHospitalId(), e.getMessage());
                    throw new IllegalArgumentException(
                            "L'hopital avec l'id " + userDTO.getHospitalId() + " n'existe pas"
                    );
                }
            }

            // ✅ Encoder le mot de passe s'il est fourni et n'est pas déjà encodé
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                // Vérifier si le mot de passe n'est pas déjà encodé (format BCrypt commence par $2a$ ou $2b$)
                if (!updatedUser.getPassword().startsWith("$2a$") && !updatedUser.getPassword().startsWith("$2b$")) {
                    updatedUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    log.info("Mot de passe encodé lors de la mise à jour pour l'utilisateur ID: {}", id);
                }
            } else {
                // Si aucun mot de passe n'est fourni, conserver l'ancien mot de passe
                updatedUser.setPassword(existingUser.getPassword());
            }

            // Conserver l'id
            updatedUser.setId(existingUser.getId());

            User savedUser = repository.save(updatedUser);
            log.info("Utilisateur mis à jour avec succès - ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

            return mapper.toDto(savedUser);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour de l'utilisateur ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


}
