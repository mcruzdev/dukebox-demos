package dev.matheuscruz;

import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {
    record OrderItemRequest(Long id, String name, double price) {
    }
}
