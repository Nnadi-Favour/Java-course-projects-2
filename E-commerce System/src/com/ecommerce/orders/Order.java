package com.ecommerce.orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ecommerce.Customer;
import com.ecommerce.Product;

public class Order {

    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }

    private final String orderId;
    private final Customer customer;
    private final List<Product> products;
    private final double orderTotal;
    private OrderStatus status;

    public Order(
            String orderId,
            Customer customer,
            List<Product> products) {

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty.");
        }

        if (customer == null) {
            throw new IllegalArgumentException(
                    "Customer cannot be null."
            );
        }

        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException(
                    "An order must contain at least one product."
            );
        }

        this.orderId = orderId;
        this.customer = customer;
        this.products = new ArrayList<>(products);

        double total = 0.0;

        for (Product product : products) {
            if (product == null) {
                throw new IllegalArgumentException(
                        "Order cannot contain a null product."
                );
            }

            total += product.getPrice();
        }

        this.orderTotal = total;
        this.status = OrderStatus.PENDING;
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void updateStatus(OrderStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Order status cannot be null."
            );
        }

        this.status = status;
    }

    public String generateSummary() {

        StringBuilder summary = new StringBuilder();

        summary.append("\nOrder ID: ")
                .append(orderId)
                .append("\n");

        summary.append("Customer: ")
                .append(customer.getName())
                .append("\n");

        summary.append("Products:\n");

        for (Product product : products) {
            summary.append("- ")
                    .append(product.getName())
                    .append(" (₦")
                    .append(String.format("%.2f", product.getPrice()))
                    .append(")\n");
        }

        summary.append("Order Total: ₦")
                .append(String.format("%.2f", orderTotal))
                .append("\n");

        summary.append("Status: ")
                .append(status)
                .append("\n");

        return summary.toString();
    }

    @Override
    public String toString() {
        return generateSummary();
    }
}