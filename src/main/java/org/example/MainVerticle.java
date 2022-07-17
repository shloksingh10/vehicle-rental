package org.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import org.example.services.BranchService;
import org.example.services.VehicleService;

public class MainVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) {
        int WORKER_POOL_SIZE = 20;
        DeploymentOptions serverOpts = new DeploymentOptions()
                .setWorkerPoolSize(WORKER_POOL_SIZE);

        DeploymentOptions workerOpts = new DeploymentOptions()
                .setWorker(true)
                .setInstances(WORKER_POOL_SIZE)
                .setWorkerPoolSize(WORKER_POOL_SIZE);

        CompositeFuture.all(
                deployVerticle(HttpVerticle.class.getName(), serverOpts),
                deployVerticle(BranchService.class.getName(), workerOpts),
                deployVerticle(VehicleService.class.getName(), workerOpts)
        ).setHandler(f ->{
            if (f.succeeded()) {
                startFuture.complete();
            }else{
                startFuture.fail(f.cause());
            }
        });
    }

    Future<Void> deployVerticle(String verticleName, DeploymentOptions opts) {
        Future<Void> retVal = Future.future();
        vertx.deployVerticle(verticleName, opts, event -> {
            if (event.succeeded()) {
                retVal.complete();
            }else{
                retVal.fail(event.cause());
            }
        });
        return retVal;
    }
}
