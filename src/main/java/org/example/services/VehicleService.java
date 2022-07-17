package org.example.services;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import org.example.branch.Branch;
import org.example.dao.BranchDAO;
import org.example.dao.VehicleDAO;
import org.example.events.VehicleEvents;
import org.example.vehicle.Vehicle;
import org.example.vehicle.VehicleFactory;

public class VehicleService extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) {
        EventBus eventBus = vertx.eventBus();
        eventBus.consumer(VehicleEvents.ADD_NEW_VEHICLE_EVENT, this::addNewVehicle);
        eventBus.consumer(VehicleEvents.RENT_VEHICLE, this::rentVehicle);
        startFuture.complete();
    }

    private void addNewVehicle(Message<JsonObject> message) {
        try {
            JsonObject payload = message.body();
            Vehicle.Type vehicleType = Vehicle.Type.valueOf(payload.getString("type"));
            String registrationNumber = payload.getString("registrationNumber");
            Long price = payload.getLong("price");
            Long branchId = payload.getLong("branchId");
            Vehicle vehicle = VehicleFactory.getVehicle(vehicleType).getVehicle(vehicleType, registrationNumber, price);
            BranchDAO branchDAO = BranchDAO.getBranchDAO();
            Branch branch = branchDAO.getBranchByBranchId(branchId);
            VehicleDAO vehicleDAO = VehicleDAO.getVehicleDAO();
            vehicleDAO.addVehicle(vehicle, branch);
            message.reply(null);
        } catch (Exception e) {
            message.fail(1, e.getMessage());
        }
    }

    private void rentVehicle(Message<JsonObject> message) {
        try {
            JsonObject payload = message.body();
            String registrationNumber = payload.getString("registrationNumber");
            Long bookingStartTime = payload.getLong("bookingStartTime");
            Long bookingEndTime = payload.getLong("bookingEndTime");
            VehicleDAO vehicleDAO = VehicleDAO.getVehicleDAO();
            vehicleDAO.bookVehicle(registrationNumber, bookingStartTime, bookingEndTime);
            JsonObject jsonObject = new JsonObject().put("data", "Booked successfully");
            message.reply(jsonObject.encodePrettily());
        } catch (Exception e) {
            message.fail(1, e.getMessage());
        }
    }
}
