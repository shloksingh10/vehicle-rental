package org.example.vehicle;

public class VanVehicle extends Vehicle{
    @Override
    public Type getType() {
        return Type.VAN;
    }

    @Override
    public Long getMinimumRentalTimeInHoursDefault() {
        return 12l;
    }

    @Override
    public Long getMaximumRentalTimeInHoursDefault() {
        return 48l;
    }
}
