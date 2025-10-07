package dev.matheuscruz;

import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.dapr.client.domain.State;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

import java.util.Objects;
import java.util.UUID;

@Path("/api/orders")
public class OrderResource {

    @Inject
    DaprClient dapr;

    @POST
    @Produces(value = "application/json")
    @Consumes(value = "application/json")
    public Response create(OrderRequest request) {
        Log.info("Receiving request for creating an Order: " + request);

        String uuid = UUID.randomUUID().toString();

        Order order = new Order(
                uuid, request.items().stream().map(i -> new Order.OrderItem(i.id(), i.name(), i.price())).toList()
        );

        /* state.yaml: metadata.name */
        dapr.saveState("postgres", uuid, order).block();

        /* pubsub.yaml: metadata.name */
        dapr.publishEvent("rabbitmq", "order.created", order).block();

        Log.info("Event (order.created) sent to 'rabbitmq'");

        return Response.ok(new OrderResponse(
                order.id(),
                order.status(),
                order.items().stream().map(item ->
                        new OrderRequest.OrderItemRequest(item.id(), item.name(), item.price())).toList()
        )).build();
    }

    @GET
    @Path("/{orderId}")
    public Response findById(@PathParam("orderId") String orderId) {
        State<Order> state = this.dapr.getState("postgres", orderId, Order.class)
                .block();
        if (Objects.isNull(state.getValue())) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(state.getValue()).build();
    }

    @POST
    @Path("/webhook/prepared")
    @Topic(pubsubName = "rabbitmq", name = "order.prepared")
    public Response orderPrepared(String event) {
        Log.info("Status changed to 'order.prepared': " + event);
        JsonObject json = new JsonObject(event);
        Order order = json.getJsonObject("data").mapTo(Order.class);
        dapr.saveState("postgres", order.id(), new Order(
                order.id(), OrderStatus.PREPARED, order.items()
        )).block();
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/in-transit")
    @Topic(pubsubName = "rabbitmq", name = "order.in-transit")
    public Response orderInTransit(String event) {
        Log.info("Status changed to 'order.in-transit': " + event);
        JsonObject json = new JsonObject(event);
        Order order = json.getJsonObject("data").mapTo(Order.class);
        dapr.saveState("postgres", order.id(), new Order(
                order.id(), OrderStatus.IN_TRANSIT, order.items()
        )).block();
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/delivered")
    @Topic(pubsubName = "rabbitmq", name = "order.delivered")
    public Response orderDelivered(String event) {
        Log.info("Status changed to 'order.delivered': " + event);
        JsonObject json = new JsonObject(event);
        Order order = json.getJsonObject("data").mapTo(Order.class);

        dapr.saveState("postgres", order.id(), new Order(
                order.id(), OrderStatus.DELIVERED, order.items()
        )).block();
        return Response.ok().build();
    }
}
