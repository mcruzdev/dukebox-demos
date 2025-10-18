package dev.matheuscruz;

import io.dapr.Topic;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/api/orders")
public class OrderResource {

    @Inject
    @RestClient
    OrderRestClient orderRestClient;

    @ConfigProperty(name = "quarkus.rest-client.orders.url")
    String sidecarUrl;

    @POST
    public Response order(OrderRequest request) {
        Log.info(request);
        Log.info("Sending request to: " + sidecarUrl);
        // The /orders/outbox in orders-api uses the Outbox pattern
        // See more here: https://docs.dapr.io/developing-applications/building-blocks/state-management/howto-outbox/
        OrderResponse response = this.orderRestClient.sendOrderTx(request);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{orderId}")
    public Response byId(@PathParam("orderId") String orderId) {
        OrderResponse response = this.orderRestClient.findById(orderId);
        return Response.ok(response).build();
    }

    @POST
    @Path("/webhook/created")
    @Topic(pubsubName = "rabbitmq", name = "order.created")
    public Response orderCreated(String event) {
        Log.info("Status changed to 'order.created': " + event);
        JsonObject json = new JsonObject(event);
        DukeBoxWebSocket.getSession("default")
                .ifPresent(session -> session.getAsyncRemote().sendText(json.getJsonObject("data").encode()));
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/prepared")
    @Topic(pubsubName = "rabbitmq", name = "order.prepared")
    public Response orderPrepared(String event) {
        Log.info("Status changed to 'order.prepared': " + event);
        JsonObject json = new JsonObject(event);
        DukeBoxWebSocket.getSession("default")
                .ifPresent(session -> session.getAsyncRemote().sendText(json.getJsonObject("data").encode()));
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/in-transit")
    @Topic(pubsubName = "rabbitmq", name = "order.in-transit")
    public Response orderInTransit(String event) {
        Log.info("Status changed to 'order.in-transit': " + event);
        JsonObject json = new JsonObject(event);
        DukeBoxWebSocket.getSession("default")
                .ifPresent(session -> session.getAsyncRemote().sendText(json.getJsonObject("data").encode()));
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/delivered")
    @Topic(pubsubName = "rabbitmq", name = "order.delivered")
    public Response orderDelivered(String event) {
        Log.info("Status changed to 'order.delivered': " + event);
        JsonObject json = new JsonObject(event);
        DukeBoxWebSocket.getSession("default")
                .ifPresent(session -> session.getAsyncRemote().sendText(json.getJsonObject("data").encode()));
        return Response.ok().build();
    }
}
