package com.app.donationService.Repository;

import com.app.donationService.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByUserId(Long userId);

    @Query("select avg(r.rating) from Review r where r.userId = :userId")
    Double averageByUserId(Long userId);

    @Query("select avg(r.rating) from Review r")
    Double averageAll();

    Optional<Review> findById(Long id);
}
