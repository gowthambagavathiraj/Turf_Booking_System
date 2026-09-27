package com.turfbooking.repository;

import com.turfbooking.model.Turf;
import com.turfbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TurfRepository extends JpaRepository<Turf, Long> {
    List<Turf> findByOwner(User owner);
    List<Turf> findByOwnerId(Long ownerId);

    @Query("SELECT t FROM Turf t WHERE " +
           "(:city IS NULL OR LOWER(t.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:sportType IS NULL OR LOWER(t.sportType) = LOWER(:sportType)) AND " +
           "(:query IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.address) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:maxPrice IS NULL OR t.pricePerHour <= :maxPrice)")
    List<Turf> searchTurfs(@Param("city") String city,
                           @Param("sportType") String sportType,
                           @Param("query") String query,
                           @Param("maxPrice") Double maxPrice);
}
