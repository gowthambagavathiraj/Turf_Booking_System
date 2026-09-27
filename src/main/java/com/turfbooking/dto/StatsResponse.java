package com.turfbooking.dto;

public class StatsResponse {
    private long totalTurfs;
    private long totalBookings;
    private long totalUsers;
    private double totalRevenue;
    private long confirmedBookings;
    private long cancelledBookings;

    public StatsResponse() {}

    public StatsResponse(long totalTurfs, long totalBookings, long totalUsers, double totalRevenue, long confirmedBookings, long cancelledBookings) {
        this.totalTurfs = totalTurfs;
        this.totalBookings = totalBookings;
        this.totalUsers = totalUsers;
        this.totalRevenue = totalRevenue;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    public long getTotalTurfs() {
        return totalTurfs;
    }

    public void setTotalTurfs(long totalTurfs) {
        this.totalTurfs = totalTurfs;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getConfirmedBookings() {
        return confirmedBookings;
    }

    public void setConfirmedBookings(long confirmedBookings) {
        this.confirmedBookings = confirmedBookings;
    }

    public long getCancelledBookings() {
        return cancelledBookings;
    }

    public void setCancelledBookings(long cancelledBookings) {
        this.cancelledBookings = cancelledBookings;
    }
}
