package dev.matheuscruz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dapr.Topic;
import io.dapr.client.DaprClient;
import io.quarkus.logging.Log;
import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.time.Duration;

@Path("/webhook/orders")
public class OrderWebhookResource {

    @Inject
    DaprClient daprClient;

    @Inject
    ObjectMapper mapper;

    @POST
    @Topic(pubsubName = "rabbitmq", name = "order.created")
    public void consume(String cloudEvent) throws JsonProcessingException {

        // See https://github.com/quarkiverse/quarkus-dapr/issues/336
        JsonObject json = new JsonObject(cloudEvent);

        Order order = json.getJsonObject("data").mapTo(Order.class);

        Log.info("Handling order: " + order);

        new Thread(() -> {

            trySleepSeconds(5);

            Log.info("Sending OrderPrepared event: " + order);

            Order prepared = new Order(order.id(), OrderStatus.PREPARED, order.items());
            daprClient.publishEvent("rabbitmq", "order.prepared", prepared)
                    .block();

            Log.info("Preparing Order with ID: " + order.id());

            trySleepSeconds(3);

            Log.info("Sending OrderInTransit event for Order: " + order);
            Order inTransit = new Order(order.id(), OrderStatus.IN_TRANSIT, order.items());
            daprClient.publishEvent("rabbitmq", "order.in-transit", inTransit)
                    .block();

            trySleepSeconds(7);

            Log.info("Sending OrderDelivered event for Order: " + order);
            Order delivered = new Order(order.id(), OrderStatus.DELIVERED, order.items());
            daprClient.publishEvent("rabbitmq", "order.delivered", delivered)
                    .block();

        }).start();
    }

    private static void trySleepSeconds(int seconds) {
        try {
            Thread.sleep(Duration.ofSeconds(seconds).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
