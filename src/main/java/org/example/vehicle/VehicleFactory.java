package org.example.vehicle;

public class VehicleFactory {
    public static Vehicle getVehicle(Vehicle.Type type) {
        switch (type) {
            case CAR:
                return new CarVehicle();
            case BIKE:
                return new BikeVehicle();
            case VAN:
                return new VanVehicle();
            default:
                return null;
        }
    }
}
