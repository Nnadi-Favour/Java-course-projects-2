import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.ecommerce.Customer;
import com.ecommerce.Product;
import com.ecommerce.orders.Order;

public class Unit2 {

    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Product> inventory = new ArrayList<>();

    private static int nextOrderNumber = 1001;

    public static void main(String[] args) {

        loadSampleProducts();

        System.out.println("==================================");
        System.out.println("       E-COMMERCE SYSTEM");
        System.out.println("==================================");

        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine().trim();

        while (customerName.isEmpty()) {
            System.out.print("Name cannot be empty. Enter your name: ");
            customerName = scanner.nextLine().trim();
        }

        Customer customer = new Customer("CUST-001", customerName);

        boolean running = true;

        while (running) {
            displayMenu();

            int choice = readInteger("Select an option: ");

            switch (choice) {
                case 1:
                    displayProducts();
                    break;

                case 2:
                    addProductToCart(customer);
                    break;

                case 3:
                    removeProductFromCart(customer);
                    break;

                case 4:
                    displayCart(customer);
                    break;

                case 5:
                    checkout(customer);
                    break;

                case 6:
                    System.out.println("Thank you for using the E-commerce System!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please select 1-6.");
            }
        }

        scanner.close();
    }

    private static void loadSampleProducts() {
        inventory.add(new Product("P001", "Laptop", 450000.00, 5));
        inventory.add(new Product("P002", "Wireless Mouse", 8500.00, 20));
        inventory.add(new Product("P003", "Keyboard", 15000.00, 10));
        inventory.add(new Product("P004", "Headphones", 25000.00, 8));
        inventory.add(new Product("P005", "USB Flash Drive", 7000.00, 15));
    }

    private static void displayMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. View Products");
        System.out.println("2. Add Product to Cart");
        System.out.println("3. Remove Product from Cart");
        System.out.println("4. View Shopping Cart");
        System.out.println("5. Place Order / Checkout");
        System.out.println("6. Exit");
        System.out.println("===============================");
    }

    private static void displayProducts() {
        System.out.println("\n========== AVAILABLE PRODUCTS ==========");

        for (Product product : inventory) {
            System.out.println(product);
        }
    }

    private static Product findProduct(String productId) {
        for (Product product : inventory) {
            if (product.getProductId().equalsIgnoreCase(productId)) {
                return product;
            }
        }

        return null;
    }

    private static int getCartQuantity(Customer customer, Product product) {
        int quantity = 0;

        for (Product item : customer.getShoppingCart()) {
            if (item == product) {
                quantity++;
            }
        }

        return quantity;
    }

    private static void addProductToCart(Customer customer) {
        displayProducts();

        System.out.print("\nEnter the Product ID to add: ");
        String productId = scanner.nextLine().trim();

        Product product = findProduct(productId);

        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        int currentQuantity = getCartQuantity(customer, product);

        if (currentQuantity >= product.getStockQuantity()) {
            System.out.println("You cannot add more than the available stock.");
            return;
        }

        customer.addProduct(product);

        System.out.println(product.getName() + " added to your cart.");
    }

    private static void removeProductFromCart(Customer customer) {
        if (customer.getShoppingCart().isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }

        displayCart(customer);

        System.out.print("\nEnter the Product ID to remove: ");
        String productId = scanner.nextLine().trim();

        try {
            customer.removeProduct(productId);
            System.out.println("Product removed from your cart.");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Unable to remove product: " + e.getMessage());
        }
    }

    private static void displayCart(Customer customer) {
        System.out.println("\n========== SHOPPING CART ==========");

        if (customer.getShoppingCart().isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }

        List<Product> displayedProducts = new ArrayList<>();

        for (Product product : customer.getShoppingCart()) {
            if (!displayedProducts.contains(product)) {
                displayedProducts.add(product);

                int quantity = getCartQuantity(customer, product);
                double subtotal = quantity * product.getPrice();

                System.out.printf(
                        "%s | Quantity: %d | Subtotal: ₦%.2f%n",
                        product.getName(),
                        quantity,
                        subtotal
                );
            }
        }

        System.out.printf("Cart Total: ₦%.2f%n", customer.calculateTotal());
    }

    private static void checkout(Customer customer) {
        if (customer.getShoppingCart().isEmpty()) {
            System.out.println("Your cart is empty. Add products before checkout.");
            return;
        }

        displayCart(customer);

        System.out.print("\nConfirm order? (Y/N): ");
        String confirmation = scanner.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Checkout cancelled.");
            return;
        }

        String orderId = "ORD-" + nextOrderNumber;

        try {
            Order order = customer.placeOrder(orderId);
            nextOrderNumber++;

            System.out.println("\n========== ORDER CONFIRMED ==========");
            System.out.println(order.generateSummary());

            System.out.println("========== UPDATED INVENTORY ==========");
            displayProducts();

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Order could not be placed: " + e.getMessage());
        }
    }

    private static int readInteger(String prompt) {
        while (true) {
            System.out.print(prompt);

            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
