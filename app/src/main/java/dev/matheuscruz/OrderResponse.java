package dev.matheuscruz;

import java.util.List;

public record OrderResponse(String id, String status, List<OrderRequest.OrderItemRequest> items) {
}
