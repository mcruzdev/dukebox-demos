package dev.matheuscruz;

import java.util.List;


public record Order(String id, OrderStatus status, List<OrderItem> items) {

    public Order(String id, List<OrderItem> items) {
        this(id, OrderStatus.RECEIVED, items);
    }

    public record OrderItem(Long id, String name, double price) {
    }
}
