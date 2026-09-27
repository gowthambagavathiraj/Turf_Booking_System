package com.turfbooking.service;

import com.turfbooking.dto.SlotGenerateRequest;
import com.turfbooking.model.Role;
import com.turfbooking.model.Slot;
import com.turfbooking.model.SlotStatus;
import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import com.turfbooking.repository.SlotRepository;
import com.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SlotService {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private AuthService authService;

    @Transactional
    public synchronized List<Slot> getSlotsByTurfAndDate(Long turfId, LocalDate date) {
        Turf turf = turfRepository.findById(turfId)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + turfId));

        List<Slot> slots = slotRepository.findByTurfAndSlotDateOrderByStartTimeAsc(turf, date);
        if (slots.isEmpty()) {
            slots = generateDailySlots(turf, date, 60, turf.getPricePerHour());
        }
        return slots;
    }

    @Transactional
    public synchronized List<Slot> generateDailySlots(Turf turf, LocalDate date, int durationMinutes, Double price) {
        List<Slot> existing = slotRepository.findByTurfAndSlotDateOrderByStartTimeAsc(turf, date);
        if (!existing.isEmpty()) {
            return existing;
        }

        List<Slot> createdSlots = new ArrayList<>();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime openTime = LocalTime.parse(turf.getOpenTime() != null ? turf.getOpenTime() : "06:00", timeFormatter);
        LocalTime closeTime = LocalTime.parse(turf.getCloseTime() != null ? turf.getCloseTime() : "23:00", timeFormatter);

        LocalTime current = openTime;
        while (current.plusMinutes(durationMinutes).isBefore(closeTime) || current.plusMinutes(durationMinutes).equals(closeTime)) {
            LocalTime next = current.plusMinutes(durationMinutes);
            String startStr = current.format(timeFormatter);
            String endStr = next.format(timeFormatter);

            if (slotRepository.findByTurfAndSlotDateAndStartTime(turf, date, startStr).isEmpty()) {
                Slot slot = new Slot(turf, date, startStr, endStr, price != null ? price : turf.getPricePerHour(), SlotStatus.AVAILABLE);
                createdSlots.add(slotRepository.save(slot));
            }
            current = next;
        }
        return slotRepository.findByTurfAndSlotDateOrderByStartTimeAsc(turf, date);
    }

    @Transactional
    public List<Slot> generateSlotsRange(SlotGenerateRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Turf turf = turfRepository.findById(request.getTurfId())
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + request.getTurfId()));

        if (!turf.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Unauthorized: You do not own this turf");
        }

        List<Slot> allGenerated = new ArrayList<>();
        LocalDate currDate = request.getStartDate();
        while (!currDate.isAfter(request.getEndDate())) {
            List<Slot> daily = generateDailySlots(turf, currDate, 
                    request.getSlotDurationMinutes() != null ? request.getSlotDurationMinutes() : 60,
                    request.getCustomPrice() != null ? request.getCustomPrice() : turf.getPricePerHour());
            allGenerated.addAll(daily);
            currDate = currDate.plusDays(1);
        }

        return allGenerated;
    }

    @Transactional
    public Slot toggleSlotBlock(Long slotId) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Slot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + slotId));

        Turf turf = slot.getTurf();
        if (!turf.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Unauthorized: You do not own this turf");
        }

        if (slot.getStatus() == SlotStatus.BOOKED) {
            throw new RuntimeException("Cannot block an already booked slot");
        }

        slot.setStatus(slot.getStatus() == SlotStatus.BLOCKED ? SlotStatus.AVAILABLE : SlotStatus.BLOCKED);
        return slotRepository.save(slot);
    }
}
