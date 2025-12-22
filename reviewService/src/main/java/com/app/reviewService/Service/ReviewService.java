package com.app.donationService.Service;

import com.app.donationService.DTO.ReviewDTO;
import com.app.donationService.Entity.Review;
import com.app.donationService.Repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private ReviewDTO toDto(Review review) {
        return new ReviewDTO(review.getId(), review.getRating(), review.getUserId());
    }

    private Review toEntity(ReviewDTO dto) {
        Review review = new Review();
        review.setId(dto.id());
        review.setRating(dto.rating());
        review.setUserId(dto.userId());
        return review;
    }

    public ReviewDTO getReviewById(Long id){
        log.info("Récupération de la review ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        return toDto(review);
    }

    public List<ReviewDTO> getAllReviews() {
        log.info("Récupération de toutes les reviews");
        return reviewRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByUser(Long userId) {
        log.info("Récupération des reviews pour l'utilisateur ID: {}", userId);
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Double getAverageByUser(Long userId) {
        log.info("Calcul de la moyenne des ratings pour l'utilisateur ID: {}", userId);
        Double avg = reviewRepository.averageByUserId(userId);
        return avg != null ? avg : 0.0;
    }

    public Double getAverageAll() {
        log.info("Calcul de la moyenne des ratings pour tous les utilisateurs");
        Double avg = reviewRepository.averageAll();
        return avg != null ? avg : 0.0;
    }

    public ReviewDTO createReview(ReviewDTO dto) {
        log.info("Création d'une review pour l'utilisateur ID: {}", dto.userId());
        Review saved = reviewRepository.save(toEntity(dto));
        return toDto(saved);
    }
}
