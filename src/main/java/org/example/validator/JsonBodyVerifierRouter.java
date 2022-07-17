package org.example.validator;
import io.vertx.ext.web.RoutingContext;

public final class JsonBodyVerifierRouter {
    public static void handle(RoutingContext routingContext) {
        try {
            Object requestItem = routingContext.getBodyAsJson();
            routingContext.next();
        } catch (Exception e) {
            routingContext.response()
                    .setStatusCode(400)
                    .end(e.toString());
        }
        return;
    }
}
