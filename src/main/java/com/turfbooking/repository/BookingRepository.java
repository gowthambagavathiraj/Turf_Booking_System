package com.turfbooking.repository;

import com.turfbooking.model.Booking;
import com.turfbooking.model.BookingStatus;
import com.turfbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserOrderByCreatedAtDesc(User user);
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findByTurfIdOrderByBookingDateDescStartTimeDesc(Long turfId);
    List<Booking> findByTurfIdAndBookingDate(Long turfId, LocalDate bookingDate);
    Optional<Booking> findByBookingReference(String bookingReference);
    long countByStatus(BookingStatus status);
}
