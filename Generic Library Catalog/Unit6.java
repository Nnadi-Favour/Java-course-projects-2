import java.util.Scanner;

public class Unit6 {

    public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        Catalog<LibraryItem<?>> catalog = new Catalog<>();

        int choice;

        do {

            System.out.println("\n===== LIBRARY CATALOG =====");
            System.out.println("1. Add Book");
            System.out.println("2. Add DVD");
            System.out.println("3. Remove Item");
            System.out.println("4. Search Item");
            System.out.println("5. View Catalog");
            System.out.println("6. Exit");

            System.out.print("Enter choice: ");
            choice = input.nextInt();
            input.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Item ID: ");
                    String id = input.nextLine();

                    System.out.print("Title: ");
                    String title = input.nextLine();

                    System.out.print("Author: ");
                    String author = input.nextLine();

                    System.out.print("Genre: ");
                    String genre = input.nextLine();

                    catalog.addItem(
                            new LibraryItem<>(id, title, author, new Book(genre)));

                    break;

                case 2:

                    System.out.print("Item ID: ");
                    id = input.nextLine();

                    System.out.print("Title: ");
                    title = input.nextLine();

                    System.out.print("Author: ");
                    author = input.nextLine();

                    System.out.print("Duration (minutes): ");
                    int duration = input.nextInt();
                    input.nextLine();

                    catalog.addItem(
                            new LibraryItem<>(id, title, author, new DVD(duration)));

                    break;

                case 3:

                    System.out.print("Enter Item ID: ");
                    catalog.removeItem(input.nextLine());

                    break;

                case 4:

                    System.out.print("Enter Item ID: ");
                    catalog.searchItem(input.nextLine());

                    break;

                case 5:

                    catalog.displayCatalog();

                    break;

                case 6:

                    System.out.println("Program terminated.");

                    break;

                default:

                    System.out.println("Invalid choice.");

            }

        } while (choice != 6);

        input.close();
    }
}