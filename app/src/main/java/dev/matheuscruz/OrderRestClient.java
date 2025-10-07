package dev.matheuscruz;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/orders")
@RegisterRestClient(configKey = "orders")
@ClientHeaderParam(name = "Content-Type", value = "application/json")
@ClientHeaderParam(name = "dapr-app-id", value = "orders-api")
public interface OrderRestClient {

    @POST
    OrderResponse sendOrder(OrderRequest request);

    @GET
    @Path("/{orderId}")
    OrderResponse findById(@PathParam("orderId") String orderId);
}

