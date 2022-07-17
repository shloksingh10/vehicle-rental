package org.example.services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.example.branch.Branch;
import org.example.dao.BranchDAO;
import org.example.dao.VehicleBranchRelationDAO;
import org.example.events.BranchEvents;
import org.example.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class BranchService extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        MessageConsumer<JsonObject> addBranchConsumer = eventBus.consumer(BranchEvents.ADD_NEW_BRANCH_EVENTS);
        addBranchConsumer.handler(this::addBranch);
        MessageConsumer<JsonObject> displayVehiclesConsumer = eventBus.consumer(BranchEvents.DISPLAY_VEHICLES_FOR_BRANCH);
        displayVehiclesConsumer.handler(this::getVehicleByBranchId);
        startFuture.complete();
    }
    public void addBranch(Message<JsonObject> message ) {
        try {
            Long branchId = message.body().getLong("branchId");
            String branchName = message.body().getString("branchName");
            List<Vehicle.Type> supportedVehicleTypes = convertSupportedVehicleTypesFromJsonArrayToListOfString(message.body().getJsonArray("supportedVehicleTypes"));
            Branch branch = Branch.builder().id(branchId).name(branchName).supportedVehicleTypes(supportedVehicleTypes).build();
            BranchDAO branchDAO = BranchDAO.getBranchDAO();
            Branch branchFromMemory = branchDAO.getBranchByBranchId(branchId);
            if (branchFromMemory != null) {
                message.fail(1, "BranchId already exists");
            }
            branchDAO.upsertBranch(branch);
            message.reply(null);
        } catch (Exception e) {
            message.fail(1, e.getMessage());
        }
    }

    public void getVehicleByBranchId(Message<JsonObject> message) {
        try {
            Long branchId = message.body().getLong("branchId");
            VehicleBranchRelationDAO vehicleBranchRelationDAO = VehicleBranchRelationDAO.getVehicleBranchRelationDAO();
            List<Vehicle> vehicles = vehicleBranchRelationDAO.getVehicleByBranchId(branchId);
            vehicles.sort(Comparator.comparing(Vehicle::getPrice));
            JsonArray jsonArray = new JsonArray(vehicles);
            message.reply(jsonArray.encodePrettily());
        } catch (Exception e) {
            message.fail(1, e.getMessage());
        }
    }
    private List<Vehicle.Type> convertSupportedVehicleTypesFromJsonArrayToListOfString(JsonArray supportedVehicleTypes) {
        List<Vehicle.Type> toRet = new ArrayList<>(Arrays.asList());
        for (int i = 0; i < supportedVehicleTypes.size(); ++i) {
            toRet.add(Vehicle.Type.valueOf(supportedVehicleTypes.getString(i)));
        }
        return toRet;
    }
}
