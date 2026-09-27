package com.turfbooking.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "turfs")
public class Turf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "sport_type", nullable = false, length = 50)
    private String sportType;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(name = "price_per_hour", nullable = false)
    private Double pricePerHour;

    @Column(name = "price_without_lights")
    private Double priceWithoutLights;

    @Column(name = "price_with_lights")
    private Double priceWithLights;

    @Column(name = "open_time", nullable = false, length = 10)
    private String openTime;

    @Column(name = "close_time", nullable = false, length = 10)
    private String closeTime;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    private Double rating = 5.0;

    private Integer totalReviews = 0;

    private LocalDateTime createdAt;

    public Turf() {
        this.createdAt = LocalDateTime.now();
        this.rating = 5.0;
        this.totalReviews = 0;
    }

    public Turf(User owner, String name, String description, String sportType, String address, 
                String city, Double pricePerHour, String openTime, String closeTime, 
                String imageUrl, String amenities) {
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.sportType = sportType;
        this.address = address;
        this.city = city;
        this.pricePerHour = pricePerHour;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.imageUrl = imageUrl;
        this.amenities = amenities;
        this.rating = 5.0;
        this.totalReviews = 0;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Double pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public Double getPriceWithoutLights() {
        return priceWithoutLights != null ? priceWithoutLights : pricePerHour;
    }

    public void setPriceWithoutLights(Double priceWithoutLights) {
        this.priceWithoutLights = priceWithoutLights;
    }

    public Double getPriceWithLights() {
        return priceWithLights != null ? priceWithLights : (pricePerHour != null ? pricePerHour * 1.3 : 2000.0);
    }

    public void setPriceWithLights(Double priceWithLights) {
        this.priceWithLights = priceWithLights;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
