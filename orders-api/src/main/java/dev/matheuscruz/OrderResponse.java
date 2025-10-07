package dev.matheuscruz;

import java.util.List;

public record OrderResponse(String id, OrderStatus status, List<OrderRequest.OrderItemRequest> items) {
}
