package org.example.vehicle;

public class BikeVehicle extends Vehicle{
    @Override
    public Type getType() {
        return Type.BIKE;
    }

    @Override
    public Long getMinimumRentalTimeInHoursDefault() {
        return 1L;
    }

    @Override
    public Long getMaximumRentalTimeInHoursDefault() {
        return 24L;
    }
}
