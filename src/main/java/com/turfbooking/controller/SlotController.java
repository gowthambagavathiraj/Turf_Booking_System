package com.turfbooking.controller;

import com.turfbooking.dto.SlotGenerateRequest;
import com.turfbooking.model.Slot;
import com.turfbooking.service.SlotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private SlotService slotService;

    @GetMapping("/turf/{turfId}")
    public ResponseEntity<?> getSlots(
            @PathVariable Long turfId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            List<Slot> slots = slotService.getSlotsByTurfAndDate(turfId, targetDate);
            return ResponseEntity.ok(slots);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> generateSlots(@Valid @RequestBody SlotGenerateRequest request) {
        try {
            List<Slot> slots = slotService.generateSlotsRange(request);
            return ResponseEntity.ok(Map.of("message", "Generated " + slots.size() + " slots successfully", "count", slots.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{slotId}/toggle-block")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> toggleBlock(@PathVariable Long slotId) {
        try {
            Slot slot = slotService.toggleSlotBlock(slotId);
            return ResponseEntity.ok(slot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
