package org.example.dao;

import org.example.branch.Branch;
import org.example.vehicle.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

public class VehicleBranchRelationDAO {
    private static Map<String, Long> vehicleRegistrationNumberToBranchIdMap;
    private static Map<Long, List<String> > branchIdToVehicleRegistrationNumbersMap;
    private static VehicleBranchRelationDAO vehicleBranchRelationDAO = null;

    private VehicleBranchRelationDAO() {
        vehicleRegistrationNumberToBranchIdMap = new LinkedHashMap<>();
        branchIdToVehicleRegistrationNumbersMap = new LinkedHashMap<>();
    }

    public static VehicleBranchRelationDAO getVehicleBranchRelationDAO() {
        if (vehicleBranchRelationDAO == null) {
            vehicleBranchRelationDAO = new VehicleBranchRelationDAO();
        }
        return vehicleBranchRelationDAO;
    }

    public void addVehicleBranch(Vehicle vehicle, Branch branch) {
        if (vehicle == null) {
            throw new NullPointerException("Vehicle can not be null");
        }
        if (branch == null) {
            throw new NullPointerException("Branch can not be null");
        }
        String registrationNumber = vehicle.getRegistrationNumber();
        if (registrationNumber == null) {
            throw new NullPointerException("RegistrationNumber can not be null");
        }
        Long branchId = branch.getId();
        if (branchId == null) {
            throw new NullPointerException("BranchId can not be null");
        }
        if (vehicleRegistrationNumberToBranchIdMap.containsKey(registrationNumber)) {
            throw new IllegalArgumentException("Registration Number already exists");
        }
        vehicleRegistrationNumberToBranchIdMap.put(registrationNumber, branchId);
        List<String> registrationNumbers = new ArrayList<>(Arrays.asList());
        if (branchIdToVehicleRegistrationNumbersMap.containsKey(branchId)) {
            registrationNumbers = branchIdToVehicleRegistrationNumbersMap.get(branchId);
        }
        registrationNumbers.add(registrationNumber);
        registrationNumbers = registrationNumbers.stream().distinct().collect(Collectors.toList());
        branchIdToVehicleRegistrationNumbersMap.put(branchId, registrationNumbers);
    }

    public List<Vehicle> getVehicleByBranchId(Long branchId) {
        if (branchId == null) {
            throw new NullPointerException("Branch id is null");
        }
        BranchDAO branchDAO = BranchDAO.getBranchDAO();
        if (!branchDAO.checkIfBranchExists(branchId)) {
            throw new IllegalArgumentException("branch id is invalid");
        }
        VehicleDAO vehicleDAO = VehicleDAO.getVehicleDAO();
        return branchIdToVehicleRegistrationNumbersMap.get(branchId).stream()
                .map(vehicleDAO::getVehicleByRegistrationNumber)
                .collect(Collectors.toList());
    }
}
