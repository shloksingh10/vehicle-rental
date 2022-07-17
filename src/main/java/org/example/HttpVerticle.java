package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import org.example.events.BranchEvents;
import org.example.events.VehicleEvents;
import org.example.validator.JsonBodyVerifierRouter;

public class HttpVerticle extends AbstractVerticle {
    public static String PORT = "8000";
    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);
        Router branchRouter = Router.router(vertx);
        Router vehicleRouter = Router.router(vertx);
        router.route().handler(CorsHandler.create("*")
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.DELETE)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("authorization")
                .allowedHeader("Content-Type"));
        router.route().handler(BodyHandler.create());

        router.route("/").handler(ctx -> {
            JsonObject payload = new JsonObject()
                    .put("ok", true)
                    .put("data", "Hello from Shlok")
                    .put("status", 200);
            HttpServerResponse response = ctx.response();
            response.setStatusCode(200);
            response.putHeader("content-type", "application/json").end(payload.encodePrettily());
        });

        branchRouter.post("/add-new-branch")
                .handler(JsonBodyVerifierRouter::handle)
                .handler(this::addNewBranch);
        branchRouter.get("/display-vehicles-for-branch")
                .handler(this::displayVehicleForBranch);
        vehicleRouter.post("/add-new-vehicle")
                .handler(JsonBodyVerifierRouter::handle)
                .handler(this::addNewVehicle);
        vehicleRouter.post("/book-vehicle")
                .handler(JsonBodyVerifierRouter::handle)
                .handler(this::bookVehicle);
        router.mountSubRouter("/branch", branchRouter);
        router.mountSubRouter("/vehicle", vehicleRouter);
        HttpServer httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router::accept).listen(Integer.parseInt(PORT), result -> {
            if (result.succeeded()) {
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });
    }

    private void bookVehicle(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();
        JsonObject payload = new JsonObject()
                .put("bookingStartTime", jsonObject.getLong("bookingStartTime"))
                .put("bookingEndTime", jsonObject.getLong("bookingEndTime"))
                .put("registrationNumber", jsonObject.getString("registrationNumber"));
        vertx.eventBus().send(VehicleEvents.RENT_VEHICLE, payload, ar -> {
            if (ar.succeeded()) {
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end((String) ar.result().body());
            } else {
                JsonObject error = new JsonObject().put("error", ar.cause().getMessage());
                routingContext.response().setStatusCode(400).end(error.encodePrettily());
            }
        });
    }

    private void displayVehicleForBranch(RoutingContext routingContext) {
        Long branchId = Long.parseLong(routingContext.queryParams().get("branch_id"));
        JsonObject payload = new JsonObject().put("branchId", branchId);
        vertx.eventBus().send(BranchEvents.DISPLAY_VEHICLES_FOR_BRANCH, payload, ar -> {
            if (ar.succeeded()) {
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end((String) ar.result().body());
            } else {
                JsonObject error = new JsonObject().put("error", ar.cause().getMessage());
                routingContext.response().setStatusCode(400).end(error.encodePrettily());
            }
        });
    }

    private void addNewVehicle(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();
        JsonObject payload = new JsonObject()
                .put("branchId", jsonObject.getLong("branchId"))
                .put("type", jsonObject.getString("type"))
                .put("price", jsonObject.getLong("price"))
                .put("registrationNumber", jsonObject.getString("registrationNumber"));
        vertx.eventBus().send(VehicleEvents.ADD_NEW_VEHICLE_EVENT, payload, ar -> {
            if (ar.succeeded()) {
                JsonObject object = new JsonObject().put("data", "Vehicle successfully added");
                routingContext.response()
                        .setStatusCode(200)
                        .putHeader("Content-Type", "application/json; charset=utf-8")
                        .end(object.encodePrettily());
            } else {
                JsonObject error = new JsonObject().put("error", ar.cause().getMessage());
                routingContext.response().setStatusCode(400).end(error.encodePrettily());
            }
        });
    }

    private void addNewBranch(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();
        JsonObject payload = new JsonObject()
                .put("branchId", jsonObject.getLong("branchId"))
                .put("branchName", jsonObject.getString("branchName"))
                .put("supportedVehicleTypes", jsonObject.getJsonArray("supportedVehicleTypes"));
        vertx.eventBus().send(BranchEvents.ADD_NEW_BRANCH_EVENTS, payload, ar -> {
           if (ar.succeeded()) {
               JsonObject object = new JsonObject().put("data", "Branch successfully added");
               routingContext.response()
                       .setStatusCode(200)
                       .putHeader("Content-Type", "application/json; charset=utf-8")
                       .end(object.encodePrettily());
           } else {
               JsonObject error = new JsonObject().put("error", ar.cause().getMessage());
               routingContext.response().setStatusCode(400).end(error.encodePrettily());
           }
        });
    }
}
