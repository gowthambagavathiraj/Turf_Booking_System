package com.turfbooking.repository;

import com.turfbooking.model.Slot;
import com.turfbooking.model.SlotStatus;
import com.turfbooking.model.Turf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    List<Slot> findByTurfAndSlotDateOrderByStartTimeAsc(Turf turf, LocalDate slotDate);
    List<Slot> findByTurfIdAndSlotDateOrderByStartTimeAsc(Long turfId, LocalDate slotDate);
    Optional<Slot> findByTurfAndSlotDateAndStartTime(Turf turf, LocalDate slotDate, String startTime);
    List<Slot> findByTurfAndSlotDateAndStatus(Turf turf, LocalDate slotDate, SlotStatus status);
}
