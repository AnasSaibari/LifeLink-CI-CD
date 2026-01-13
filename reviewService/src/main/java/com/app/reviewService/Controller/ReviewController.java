package com.app.donationService.Controller;

import com.app.donationService.DTO.ReviewDTO;
import com.app.donationService.Service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // GET review by id
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReview(@PathVariable Long id){
        log.info("Requête GET /api/reviews/{} - Récupération d'une review", id);
        ReviewDTO review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }

    // GET all reviews
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAll(){
        log.info("Requête GET /api/reviews - Récupération de toutes les reviews");
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // GET all reviews for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getByUser(@PathVariable Long userId){
        log.info("Requête GET /api/reviews/user/{} - Récupération des reviews d'un utilisateur", userId);
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    // GET average rating for a user
    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageByUser(@PathVariable Long userId){
        log.info("Requête GET /api/reviews/user/{}/average - Moyenne des ratings", userId);
        return ResponseEntity.ok(reviewService.getAverageByUser(userId));
    }

    // GET average rating for all users
    @GetMapping("/average")
    public ResponseEntity<Double> getAverageAll(){
        log.info("Requête GET /api/reviews/average - Moyenne globale des ratings");
        return ResponseEntity.ok(reviewService.getAverageAll());
    }

    // POST create a review
    @PostMapping
    public ResponseEntity<ReviewDTO> create(@RequestBody ReviewDTO dto){
        log.info("Requête POST /api/reviews - Création d'une review");
        ReviewDTO created = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
