package org.example.dao;

import org.example.branch.Branch;
import org.example.vehicle.Vehicle;

import java.util.*;

public class BranchDAO {
    private static List<Branch> branches;
    private static BranchDAO branchDAO = null;
    private BranchDAO() {
        branches = new LinkedList<>(Arrays.asList());
    }
    public static BranchDAO getBranchDAO() {
        if (branchDAO == null) {
            branchDAO = new BranchDAO();
        }
        return branchDAO;
    }

    public void upsertBranch(Branch branch) {
        Branch branchFromMemory = getBranchByBranchId(branch.getId());
        if (branchFromMemory != null) {
            deleteBranchByBranchId(branchFromMemory.getId());
        }
        insertBranch(branch);
    }

    public Branch getBranchByBranchId(Long branchId) {
        return branches.stream().filter(branch -> Objects.equals(branchId, branch.getId()))
                .findAny()
                .orElse(null);
    }

    public boolean checkIfBranchExists(Long branchId) {
        Branch branch = getBranchByBranchId(branchId);
        if (branch == null) {
            return false;
        }
        return true;
    }
    public boolean checkIfBranchSupportsVehicleType(long branchId, Vehicle.Type type) {
        Branch branch = getBranchByBranchId(branchId);
        if (branch == null) {
            throw new IllegalArgumentException("Branch doesn't exist");
        }
        return branch.getSupportedVehicleTypes().contains(type);
    }
    public boolean deleteBranchByBranchId(Long branchId) {
        return branches.removeIf(branch -> Objects.equals(branchId, branch.getId()));
    }

    private void insertBranch(Branch branch) {
        branches.add(branch);
    }
}
