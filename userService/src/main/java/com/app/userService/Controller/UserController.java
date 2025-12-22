package com.app.userService.Controller;

import com.app.userService.DTO.ReviewDTO;
import com.app.userService.DTO.UserDTO;
import com.app.userService.Entity.User;
import com.app.userService.Service.EmailVerificationService;
import com.app.userService.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    @Autowired
    private EmailVerificationService  verificationService;

    // 1. GET ALL users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Requête GET /api/users - Récupération de tous les utilisateurs");
        List<UserDTO> users = service.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 2. GET user + location
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserWithLocation(@PathVariable Long id) {
        log.info("Requête GET /api/users/{} - Récupération de l'utilisateur", id);
        UserDTO user = service.getUser(Math.toIntExact(id));
        return ResponseEntity.ok(user);
    }

    // 3. CREATE user
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("Requête POST /api/users - Création d'un nouvel utilisateur");
        User createdUser = service.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // 4. DELETE user by id
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<User> deleteUser(@PathVariable Long id) {
        log.info("Requête DELETE /api/users/{}/delete - Suppression de l'utilisateur", id);
        User deletedUser = service.deleteUserById(Math.toIntExact(id));
        return ResponseEntity.ok(deletedUser);
    }

    // 5. Update user by id
    @PutMapping("/{id}/update")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        log.info("Requête PUT /api/users/{}/update - Mise à jour de l'utilisateur", id);
        UserDTO updatedUser = service.updateUser(Math.toIntExact(id), userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 6. Reviews integration
    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        log.info("Requête GET /api/users/reviews - Toutes les reviews");
        return ResponseEntity.ok(service.getAllReviews());
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long id) {
        log.info("Requête GET /api/users/{}/reviews - Reviews par utilisateur", id);
        return ResponseEntity.ok(service.getReviewsByUser(id));
    }

    @GetMapping("/{id}/reviews/average")
    public ResponseEntity<Double> getAverageByUser(@PathVariable Long id) {
        log.info("Requête GET /api/users/{}/reviews/average - Moyenne par utilisateur", id);
        return ResponseEntity.ok(service.getAverageRatingByUser(id));
    }

    @GetMapping("/reviews/average")
    public ResponseEntity<Double> getAverageAll() {
        log.info("Requête GET /api/users/reviews/average - Moyenne globale");
        return ResponseEntity.ok(service.getAverageRatingAllUsers());
    }
}
