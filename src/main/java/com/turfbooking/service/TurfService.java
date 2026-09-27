package com.turfbooking.service;

import com.turfbooking.dto.TurfRequest;
import com.turfbooking.model.Role;
import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import com.turfbooking.repository.TurfRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TurfService {

    @Autowired
    private TurfRepository turfRepository;

    @Autowired
    private AuthService authService;

    public List<Turf> getAllTurfs() {
        return turfRepository.findAll();
    }

    public List<Turf> searchTurfs(String city, String sportType, String query, Double maxPrice) {
        String cityParam = (city != null && !city.trim().isEmpty()) ? city.trim() : null;
        String sportParam = (sportType != null && !sportType.trim().isEmpty() && !sportType.equalsIgnoreCase("ALL")) ? sportType.trim() : null;
        String queryParam = (query != null && !query.trim().isEmpty()) ? query.trim() : null;

        return turfRepository.searchTurfs(cityParam, sportParam, queryParam, maxPrice);
    }

    public Turf getTurfById(Long id) {
        return turfRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turf not found with id: " + id));
    }

    public List<Turf> getTurfsByOwner(Long ownerId) {
        return turfRepository.findByOwnerId(ownerId);
    }

    public List<Turf> getMyTurfs() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        return turfRepository.findByOwner(currentUser);
    }

    @Transactional
    public Turf createTurf(TurfRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser.getRole() != Role.ROLE_OWNER && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Only Turf Owners or Admins can create turfs");
        }

        Turf turf = new Turf(
                currentUser,
                request.getName(),
                request.getDescription(),
                request.getSportType(),
                request.getAddress(),
                request.getCity(),
                request.getPricePerHour(),
                request.getOpenTime(),
                request.getCloseTime(),
                request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty() 
                    ? request.getImageUrl() 
                    : "https://images.unsplash.com/photo-1574629810360-7efbbe195018?auto=format&fit=crop&w=800&q=80",
                request.getAmenities()
        );

        return turfRepository.save(turf);
    }

    @Transactional
    public Turf updateTurf(Long id, TurfRequest request) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Turf turf = getTurfById(id);

        if (!turf.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Unauthorized: You do not have permission to edit this turf");
        }

        turf.setName(request.getName());
        turf.setDescription(request.getDescription());
        turf.setSportType(request.getSportType());
        turf.setAddress(request.getAddress());
        turf.setCity(request.getCity());
        turf.setPricePerHour(request.getPricePerHour());
        turf.setOpenTime(request.getOpenTime());
        turf.setCloseTime(request.getCloseTime());
        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            turf.setImageUrl(request.getImageUrl());
        }
        turf.setAmenities(request.getAmenities());

        return turfRepository.save(turf);
    }

    @Transactional
    public void deleteTurf(Long id) {
        User currentUser = authService.getCurrentAuthenticatedUser();
        Turf turf = getTurfById(id);

        if (!turf.getOwner().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new RuntimeException("Unauthorized: You do not have permission to delete this turf");
        }

        turfRepository.delete(turf);
    }
}
