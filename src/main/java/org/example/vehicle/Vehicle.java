package org.example.vehicle;

public abstract class Vehicle {
    public enum Type {
        CAR,
        BIKE,
        VAN
    }

    public enum Status {
        AVAILABLE,
        BOOKED
    }
    Type type;
    String registrationNumber;
    Long price;
    Long minimumRentalTimeInHours;
    Long maximumRentalTimeInHours;

    Status status;
    Long bookingStartTime;
    Long bookingEndTime;
    public abstract Type getType();
    public abstract Long getMinimumRentalTimeInHoursDefault();
    public abstract Long getMaximumRentalTimeInHoursDefault();

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Long getPrice() {
        return this.price;
    }

    public Long getBookingStartTime() {
        return this.bookingStartTime;
    }

    public Long getBookingEndTime() {
        return this.bookingEndTime;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setBookingStartTime(Long bookingStartTime) {
        this.bookingStartTime = bookingStartTime;
    }

    public void setBookingEndTime(Long bookingEndTime) {
        this.bookingEndTime = bookingEndTime;
    }

    public Vehicle getVehicle(Type type, String registrationNumber, Long price) {
        return getVehicle(type, registrationNumber, price, getMinimumRentalTimeInHoursDefault(), getMaximumRentalTimeInHoursDefault(), Status.AVAILABLE);
    }
    public Vehicle getVehicle(Type type, String registrationNumber, Long price, Long minimumRentalTimeInHours, Long maximumRentalTimeInHours, Status status) {
        this.type = type;
        this.registrationNumber = registrationNumber;
        this.price = price;
        this.minimumRentalTimeInHours = minimumRentalTimeInHours;
        this.maximumRentalTimeInHours = maximumRentalTimeInHours;
        this.status = status;
        return this;
    }
}
