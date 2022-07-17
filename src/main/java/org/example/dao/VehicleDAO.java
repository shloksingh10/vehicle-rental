package org.example.dao;

import org.example.branch.Branch;
import org.example.vehicle.Vehicle;

import java.util.LinkedHashMap;
import java.util.Map;

public class VehicleDAO {
    private static Map<String, Vehicle> vehicleMap;
    private static VehicleDAO vehicleDAO = null;

    private VehicleDAO() {
        vehicleMap = new LinkedHashMap<>();
    }

    public static VehicleDAO getVehicleDAO() {
        if (vehicleDAO == null) {
            vehicleDAO = new VehicleDAO();
        }
        return vehicleDAO;
    }

    public void addVehicle(Vehicle vehicle, Branch branch) {
        if (checkIfVehicleExists(vehicle)) {
            throw new IllegalArgumentException("Vehicle already exists");
        }
        BranchDAO branchDAO = BranchDAO.getBranchDAO();
        VehicleBranchRelationDAO vehicleBranchRelationDAO = VehicleBranchRelationDAO.getVehicleBranchRelationDAO();
        if (branch == null) {
            throw new IllegalArgumentException("Branch does not exist");
        }
        if (!branchDAO.checkIfBranchExists(branch.getId())) {
            throw new IllegalArgumentException("Branch does not exist");
        }
        if (!branchDAO.checkIfBranchSupportsVehicleType(branch.getId(), vehicle.getType())) {
            throw new IllegalArgumentException("Branch does not support vehicle type");
        }
        vehicleMap.put(vehicle.getRegistrationNumber(), vehicle);
        vehicleBranchRelationDAO.addVehicleBranch(vehicle, branch);
    }

    public boolean checkIfVehicleExists(Vehicle vehicle) {
        return checkIfVehicleExistsByRegistrationNumber(vehicle.getRegistrationNumber());
    }

    public Vehicle getVehicleByRegistrationNumber(String registrationNumber) {
        if (checkIfVehicleExistsByRegistrationNumber(registrationNumber)) {
            return vehicleMap.get(registrationNumber);
        }
        return null;
    }



    public void bookVehicle(String registrationNumber, Long bookingStartTime, Long bookingEndTime) {
        if (!checkIfVehicleExistsByRegistrationNumber(registrationNumber)) {
            throw new IllegalArgumentException("Vehicle doesn't exist");
        }
        Vehicle vehicle = getVehicleByRegistrationNumber(registrationNumber);
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle doesn't exist");
        }
        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new IllegalArgumentException("Vehicle is booked");
        }
        vehicle.setStatus(Vehicle.Status.BOOKED);
        vehicle.setBookingStartTime(bookingStartTime);
        vehicle.setBookingEndTime(bookingEndTime);
        updateVehicle(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) {
        if (checkIfVehicleExistsByRegistrationNumber(vehicle.getRegistrationNumber())) {
            vehicleMap.remove(vehicle.getRegistrationNumber());
            vehicleMap.put(vehicle.getRegistrationNumber(), vehicle);
        }
    }
    private boolean checkIfVehicleExistsByRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null) {
            throw new NullPointerException("Registration Number cannot be null");
        }
        return vehicleMap.containsKey(registrationNumber);
    }
}
