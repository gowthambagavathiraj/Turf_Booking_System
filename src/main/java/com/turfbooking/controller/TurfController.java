package com.turfbooking.controller;

import com.turfbooking.dto.TurfRequest;
import com.turfbooking.model.Turf;
import com.turfbooking.service.TurfService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/turfs")
public class TurfController {

    @Autowired
    private TurfService turfService;

    @GetMapping
    public ResponseEntity<List<Turf>> getTurfs(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String sportType,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Double maxPrice) {
        
        if (city != null || sportType != null || query != null || maxPrice != null) {
            return ResponseEntity.ok(turfService.searchTurfs(city, sportType, query, maxPrice));
        }
        return ResponseEntity.ok(turfService.getAllTurfs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTurfById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(turfService.getTurfById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/my-turfs")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<List<Turf>> getMyTurfs() {
        return ResponseEntity.ok(turfService.getMyTurfs());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Turf>> getTurfsByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(turfService.getTurfsByOwner(ownerId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> createTurf(@Valid @RequestBody TurfRequest request) {
        try {
            Turf turf = turfService.createTurf(request);
            return ResponseEntity.ok(turf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> updateTurf(@PathVariable Long id, @Valid @RequestBody TurfRequest request) {
        try {
            Turf turf = turfService.updateTurf(id, request);
            return ResponseEntity.ok(turf);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
    public ResponseEntity<?> deleteTurf(@PathVariable Long id) {
        try {
            turfService.deleteTurf(id);
            return ResponseEntity.ok(Map.of("message", "Turf deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
