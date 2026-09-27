package com.turfbooking.dto;

import jakarta.validation.constraints.NotNull;

public class BookingRequest {
    @NotNull(message = "Turf ID is required")
    private Long turfId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;

    private String paymentMethod = "ONLINE";

    public BookingRequest() {}

    public BookingRequest(Long turfId, Long slotId, String paymentMethod) {
        this.turfId = turfId;
        this.slotId = slotId;
        this.paymentMethod = paymentMethod;
    }

    public Long getTurfId() {
        return turfId;
    }

    public void setTurfId(Long turfId) {
        this.turfId = turfId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
