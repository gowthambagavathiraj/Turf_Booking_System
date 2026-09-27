package com.turfbooking.config;

import com.turfbooking.model.*;
import com.turfbooking.repository.*;
import com.turfbooking.service.SlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private SlotService slotService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        // 1. Create Default Users
        User owner = new User("Alex Johnson (Owner)", "owner@turf.com", passwordEncoder.encode("password123"), "+1 555-0199", Role.ROLE_OWNER);
        User customer = new User("David Miller", "user@turf.com", passwordEncoder.encode("password123"), "+1 555-0144", Role.ROLE_CUSTOMER);
        User admin = new User("Turf SuperAdmin", "admin@turf.com", passwordEncoder.encode("password123"), "+1 555-0100", Role.ROLE_ADMIN);

        userRepository.saveAll(List.of(owner, customer, admin));

        // 2. Create Sample Turfs
        Turf turf1 = new Turf(
                owner,
                "Camp Nou 7v7 Football Arena",
                "FIFA standard artificial turf with international floodlighting, player dugout, locker rooms, and high-traction FIFA-approved grass blades for maximum performance.",
                "Football",
                "452 Stadium Way, Downtown Sports Complex",
                "New York",
                45.00,
                "06:00",
                "23:00",
                "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=1200&q=80",
                "FIFA Standard Turf, Floodlights, Changing Rooms, Drinking Water, Free Parking, First Aid"
        );

        Turf turf2 = new Turf(
                owner,
                "Wimbledon Box Cricket & Turf",
                "All-weather covered cricket net turf with bowling machine, night LED floodlights, premium non-slip turf matting, and digital scoreboard.",
                "Cricket",
                "88 Riverside Blvd, East Bay Area",
                "San Francisco",
                35.00,
                "07:00",
                "22:00",
                "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&w=1200&q=80",
                "Bowling Machine, Digital Scoreboard, Floodlights, Refreshment Lounge, Equipment Rental"
        );

        Turf turf3 = new Turf(
                owner,
                "Apex Indoor Badminton & Tennis Club",
                "BWF approved synthetic rubber multi-court complex with anti-glare overhead lighting, air-conditioned seating, and professional coaching availability.",
                "Badminton",
                "12 Central Park South",
                "Chicago",
                25.00,
                "06:00",
                "22:00",
                "https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?auto=format&fit=crop&w=1200&q=80",
                "Wooden Flooring, AC Waiting Lounge, BWF Approved, Shower Rooms, Racket Stringing"
        );

        Turf turf4 = new Turf(
                owner,
                "Skyline Multi-Sport Turf Dome",
                "Futuristic rooftop multi-sport turf suitable for 5v5 Futsal, Box Cricket, and Frisbee. Features panoramic skyline views, high safety netting, and audio sound system.",
                "Multi-Sport",
                "777 Metro Heights Rooftop",
                "Los Angeles",
                55.00,
                "08:00",
                "00:00",
                "https://images.unsplash.com/photo-1529900748604-07564a03e7a6?auto=format&fit=crop&w=1200&q=80",
                "Rooftop View, Bluetooth Sound System, Night Neon Lights, VIP Lounge, Cafeteria"
        );

        turfRepository.saveAll(List.of(turf1, turf2, turf3, turf4));

        // 3. Generate Slots for Today and Next 5 Days
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 5; i++) {
            LocalDate targetDate = today.plusDays(i);
            slotService.generateDailySlots(turf1, targetDate, 60, turf1.getPricePerHour());
            slotService.generateDailySlots(turf2, targetDate, 60, turf2.getPricePerHour());
            slotService.generateDailySlots(turf3, targetDate, 60, turf3.getPricePerHour());
            slotService.generateDailySlots(turf4, targetDate, 60, turf4.getPricePerHour());
        }

        // 4. Create sample booking for today
        List<Slot> todaySlotsTurf1 = slotRepository.findByTurfAndSlotDateOrderByStartTimeAsc(turf1, today);
        if (todaySlotsTurf1.size() > 3) {
            Slot bookedSlot = todaySlotsTurf1.get(2);
            bookedSlot.setStatus(SlotStatus.BOOKED);
            slotRepository.save(bookedSlot);

            Booking sampleBooking = new Booking(
                    "TRF-849201-DEMO",
                    customer,
                    turf1,
                    bookedSlot,
                    today,
                    bookedSlot.getStartTime(),
                    bookedSlot.getEndTime(),
                    bookedSlot.getPrice(),
                    BookingStatus.CONFIRMED,
                    "PAID",
                    "CREDIT_CARD"
            );
            bookingRepository.save(sampleBooking);
        }

        // 5. Add Sample Reviews
        Review rev1 = new Review(turf1, customer, 5, "Amazing surface! Played a 7v7 friendly match and the turf quality was top-tier. Great lighting too.");
        Review rev2 = new Review(turf2, customer, 5, "Best box cricket experience in town. Bowling machine speed control was super smooth!");
        Review rev3 = new Review(turf4, customer, 5, "Unbelievable view from the rooftop and great vibe at night. Will definitely book again!");
        reviewRepository.saveAll(List.of(rev1, rev2, rev3));
    }
}
