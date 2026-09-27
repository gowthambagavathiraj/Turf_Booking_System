package com.turfbooking.service;

import com.turfbooking.dto.BookingRequest;
import com.turfbooking.model.*;
import com.turfbooking.repository.BookingRepository;
import com.turfbooking.repository.SlotRepository;
import com.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public Booking createBooking(BookingRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();

        Turf turf = turfRepository.findById(request.getTurfId())
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + request.getTurfId()));

        Slot slot = slotRepository.findById(request.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + request.getSlotId()));

        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new RuntimeException("Selected slot is already booked. Please choose another slot.");
        }
        if (slot.getStatus() == SlotStatus.BLOCKED) {
            throw new RuntimeException("Selected slot is currently unavailable/blocked by the owner.");
        }

        // Mark slot as booked
        slot.setStatus(SlotStatus.BOOKED);
        slotRepository.save(slot);

        // Generate Booking Reference
        String reference = "TRF-" + System.currentTimeMillis() % 1000000 + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        Booking booking = new Booking(
                reference,
                currentUser,
                turf,
                slot,
                slot.getSlotDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getPrice(),
                BookingStatus.CONFIRMED,
                "PAID",
                request.getPaymentMethod() != null ? request.getPaymentMethod() : "ONLINE"
        );

        return bookingRepository.save(booking);
    }

    public List<Booking> getMyBookings() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return bookingRepository.findByUserOrderByCreatedAtDesc(currentUser);
    }

    public List<Booking> getBookingsByTurf(Long turfId) {
        return bookingRepository.findByTurfIdOrderByBookingDateDescStartTimeDesc(turfId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        boolean isCustomer = booking.getUser().getId().equals(currentUser.getId());
        boolean isOwner = booking.getTurf().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isCustomer && !isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized access to this booking");
        }

        return booking;
    }

    @Transactional
    public Booking cancelBooking(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        boolean isCustomer = booking.getUser().getId().equals(currentUser.getId());
        boolean isOwner = booking.getTurf().getOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isCustomer && !isOwner && !isAdmin) {
            throw new RuntimeException("Unauthorized: Cannot cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus("REFUNDED");

        // Release the slot back to AVAILABLE
        Slot slot = booking.getSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }

        return bookingRepository.save(booking);
    }
}
