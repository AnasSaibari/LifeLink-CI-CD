package com.app.userService.FeignClient;

import com.app.userService.DTO.ReviewDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "reviewService")
public interface ReviewClient {

    @GetMapping("/api/reviews")
    List<ReviewDTO> getAllReviews();

    @GetMapping("/api/reviews/user/{userId}")
    List<ReviewDTO> getReviewsByUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/reviews/user/{userId}/average")
    Double getAverageByUser(@PathVariable("userId") Long userId);

    @GetMapping("/api/reviews/average")
    Double getAverageAll();
}

