package org.example.vehicle;

public class CarVehicle extends Vehicle{
    @Override
    public Type getType() {
        return Type.CAR;
    }

    @Override
    public Long getMinimumRentalTimeInHoursDefault() {
        return 1L;
    }

    @Override
    public Long getMaximumRentalTimeInHoursDefault() {
        return 48L;
    }

}
