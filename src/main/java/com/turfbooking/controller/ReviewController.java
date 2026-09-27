package com.turfbooking.controller;

import com.turfbooking.dto.ReviewRequest;
import com.turfbooking.model.Review;
import com.turfbooking.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/turf/{turfId}")
    public ResponseEntity<List<Review>> getTurfReviews(@PathVariable Long turfId) {
        return ResponseEntity.ok(reviewService.getReviewsByTurf(turfId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReview(@Valid @RequestBody ReviewRequest request) {
        try {
            Review review = reviewService.addReview(request);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
