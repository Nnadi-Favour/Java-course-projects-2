package com.ecommerce;

public class Product {

    private final String productId;
    private String name;
    private double price;
    private int stockQuantity;

    public Product(String productId, String name, double price, int stockQuantity) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }

        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }

        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }

        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Product price cannot be negative.");
        }

        this.price = price;
    }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Stock quantity must be greater than zero."
            );
        }

        stockQuantity += quantity;
    }

    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero."
            );
        }

        if (quantity > stockQuantity) {
            throw new IllegalStateException(
                    "Not enough stock available for " + name + "."
            );
        }

        stockQuantity -= quantity;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %s - ₦%.2f - Stock: %d",
                productId,
                name,
                price,
                stockQuantity
        );
    }
}