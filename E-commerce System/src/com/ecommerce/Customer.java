package com.ecommerce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;

import com.ecommerce.orders.Order;

public class Customer {

    private final String customerId;
    private String name;
    private final List<Product> shoppingCart;

    public Customer(String customerId, String name) {
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }

        this.customerId = customerId;
        this.name = name;
        this.shoppingCart = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }

        this.name = name;
    }

    public List<Product> getShoppingCart() {
        return Collections.unmodifiableList(shoppingCart);
    }

    public void addProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }

        if (!product.isInStock()) {
            throw new IllegalStateException(
                    "Product is currently out of stock."
            );
        }

        shoppingCart.add(product);
    }

    public void removeProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }

        boolean removed = shoppingCart.removeIf(
                product -> product.getProductId().equals(productId)
        );

        if (!removed) {
            throw new IllegalStateException(
                    "Product was not found in the shopping cart."
            );
        }
    }

    public double calculateTotal() {
        double total = 0.0;

        for (Product product : shoppingCart) {
            total += product.getPrice();
        }

        return total;
    }

    public Order placeOrder(String orderId) {

        if (orderId == null || orderId.trim().isEmpty()) {
            throw new IllegalArgumentException("Order ID cannot be empty.");
        }

        if (shoppingCart.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot place an order with an empty cart."
            );
        }

        // Count the quantity required for each product.
        Map<Product, Integer> requiredStock = new IdentityHashMap<>();

        for (Product product : shoppingCart) {
            requiredStock.put(
                    product,
                    requiredStock.getOrDefault(product, 0) + 1
            );
        }

        // Validate all stock before making any changes.
        for (Map.Entry<Product, Integer> entry : requiredStock.entrySet()) {
            if (entry.getValue() > entry.getKey().getStockQuantity()) {
                throw new IllegalStateException(
                        "Not enough stock available for "
                                + entry.getKey().getName() + "."
                );
            }
        }

        // Create the order before reducing stock.
        Order order = new Order(
                orderId,
                this,
                new ArrayList<>(shoppingCart)
        );

        // Reduce stock only after successful validation and order creation.
        for (Map.Entry<Product, Integer> entry : requiredStock.entrySet()) {
            entry.getKey().reduceStock(entry.getValue());
        }

        shoppingCart.clear();

        return order;
    }

    @Override
    public String toString() {
        return customerId + " - " + name;
    }
}