import java.util.ArrayList;

public class Catalog<T extends LibraryItem<?>> {

    private ArrayList<T> items = new ArrayList<>();

    // Add item
    public void addItem(T item) {
        items.add(item);
        System.out.println("Item added successfully.");
    }

    // Remove item
    public void removeItem(String id) {

        boolean found = false;

        for (T item : items) {

            if (item.getItemID().equals(id)) {
                items.remove(item);
                System.out.println("Item removed successfully.");
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Error: Item with ID " + id + " not found.");
        }
    }

    // Search item
    public void searchItem(String id) {

        for (T item : items) {

            if (item.getItemID().equals(id)) {
                System.out.println(item);
                return;
            }
        }

        System.out.println("Item not found.");
    }

    // Display catalog
    public void displayCatalog() {

        if (items.isEmpty()) {
            System.out.println("Catalog is empty.");
            return;
        }

        for (T item : items) {
            System.out.println("-----------------------");
            System.out.println(item);
        }
    }
}