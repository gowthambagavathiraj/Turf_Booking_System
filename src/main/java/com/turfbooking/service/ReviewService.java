package com.turfbooking.service;

import com.turfbooking.dto.ReviewRequest;
import com.turfbooking.model.Review;
import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import com.turfbooking.repository.ReviewRepository;
import com.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private AuthService authService;

    public List<Review> getReviewsByTurf(Long turfId) {
        return reviewRepository.findByTurfIdOrderByCreatedAtDesc(turfId);
    }

    @Transactional
    public Review addReview(ReviewRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Turf turf = turfRepository.findById(request.getTurfId())
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + request.getTurfId()));

        Review review = new Review(turf, currentUser, request.getRating(), request.getComment());
        Review saved = reviewRepository.save(review);

        // Update Turf average rating
        List<Review> allReviews = reviewRepository.findByTurfIdOrderByCreatedAtDesc(turf.getId());
        double avg = allReviews.stream().mapToInt(Review::getRating).average().orElse(5.0);
        turf.setRating(Math.round(avg * 10.0) / 10.0);
        turf.setTotalReviews(allReviews.size());
        turfRepository.save(turf);

        return saved;
    }
}
