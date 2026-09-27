package com.turfbooking.repository;

import com.turfbooking.model.Review;
import com.turfbooking.model.Turf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTurfOrderByCreatedAtDesc(Turf turf);
    List<Review> findByTurfIdOrderByCreatedAtDesc(Long turfId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.turf.id = :turfId")
    Double calculateAverageRatingForTurf(@Param("turfId") Long turfId);
}
