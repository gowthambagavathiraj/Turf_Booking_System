package com.turfbooking.service;

import com.turfbooking.dto.StatsResponse;
import com.turfbooking.model.Booking;
import com.turfbooking.model.BookingStatus;
import com.turfbooking.model.Role;
import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import com.turfbooking.repository.BookingRepository;
import com.turfbooking.repository.TurfRepository;
import com.turfbooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public StatsResponse getPublicStats() {
        long turfs = turfRepository.count();
        long bookings = bookingRepository.count();
        long users = userRepository.count();
        double revenue = bookingRepository.findAll().stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        long confirmed = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        long cancelled = bookingRepository.countByStatus(BookingStatus.CANCELLED);

        return new StatsResponse(turfs, bookings, users, revenue, confirmed, cancelled);
    }

    public StatsResponse getOwnerStats() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        List<Turf> myTurfs = turfRepository.findByOwner(currentUser);
        List<Long> turfIds = myTurfs.stream().map(Turf::getId).toList();

        List<Booking> ownerBookings = bookingRepository.findAll().stream()
                .filter(b -> turfIds.contains(b.getTurf().getId()))
                .toList();

        long totalBookings = ownerBookings.size();
        double totalRevenue = ownerBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.COMPLETED)
                .mapToDouble(Booking::getTotalAmount)
                .sum();

        long confirmed = ownerBookings.stream().filter(b -> b.getStatus() == BookingStatus.CONFIRMED).count();
        long cancelled = ownerBookings.stream().filter(b -> b.getStatus() == BookingStatus.CANCELLED).count();
        long uniqueCustomers = ownerBookings.stream().map(b -> b.getUser().getId()).distinct().count();

        return new StatsResponse(myTurfs.size(), totalBookings, uniqueCustomers, totalRevenue, confirmed, cancelled);
    }
}
