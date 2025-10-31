package suppliermonitoring_classes;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    
    private static final ArrayList<Supplier> suppliers = new ArrayList<>();
    private static final ArrayList<Items> items = new ArrayList<>();
    private static final ArrayList<Delivers> deliveries = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printHeader("SUPPLIER MONITORING SYSTEM");
            System.out.println("1. Manage Items");
            System.out.println("2. Manage Suppliers");
            System.out.println("3. Manage Deliveries");
            System.out.println("4. Display All Records");
            System.out.println("5. Exit");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> manageItems();
                case 2 -> manageSuppliers();
                case 3 -> manageDeliveries();
                case 4 -> displayAllRecords();
                case 5 -> {
                    System.out.println("\nThank you for using the Supplier Monitoring System!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        sc.close();
    }

    private static void printHeader(String title) {
        System.out.println("========================================");
        System.out.println("      " + title);
        System.out.println("========================================");
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Supplier Management
    private static void manageSuppliers() {
        boolean back = false;
        while (!back) {
            printHeader("SUPPLIER MANAGEMENT");
            System.out.println("1. Add Supplier");
            System.out.println("2. View All Suppliers");
            System.out.println("3. Update Supplier");
            System.out.println("4. Delete Supplier");
            System.out.println("5. Back");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> addSupplier();
                case 2 -> viewSuppliers();
                case 3 -> updateSupplier();
                case 4 -> deleteSupplier();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addSupplier() {
        Supplier s = Supplier.inputSupplier();
        suppliers.add(s);
        s.insertToDatabase(); 
        System.out.println("Supplier added successfully!");
    }

    private static void viewSuppliers() {
        Supplier.viewSuppliers();
    }

    private static void updateSupplier() {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers to update.");
            return;
        }
        viewSuppliers();
        int index = getIntInput("Enter supplier number to update: ") - 1;

        if (index >= 0 && index < suppliers.size()) {
            suppliers.get(index).updateInfo(sc);
        } else {
            System.out.println("Invalid supplier number.");
        }
    }

    private static void deleteSupplier() {
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers to delete.");
            return;
        }
        viewSuppliers();
        int index = getIntInput("Enter supplier number to delete: ") - 1;

        if (index >= 0 && index < suppliers.size()) {
            suppliers.remove(index);
            System.out.println("Supplier deleted successfully!");
        } else {
            System.out.println("Invalid supplier number.");
        }
    }

    // Item Management
    private static void manageItems() {
        boolean back = false;
        while (!back) {
            printHeader("ITEM MANAGEMENT");
            System.out.println("1. Add Item");
            System.out.println("2. View All Items");
            System.out.println("3. Update Item");
            System.out.println("4. Delete Item");
            System.out.println("5. Back");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> addItem();
                case 2 -> viewItems();
                case 3 -> updateItem();
                case 4 -> deleteItem();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addItem() {
        Items i = Items.inputItem();      
        items.add(i);                     
        i.insertToDatabase_Item();        
        System.out.println("Item/s added successfully!");
    }

    
    private static void viewItems() {
         Items.viewItems();
    }

    private static void updateItem() {
        Items.updateItemInDatabase();
    }
    
    private static void deleteItem() {
        if (items.isEmpty()) {
            System.out.println("No items to delete.");
            return;
        }
        viewItems();
        int index = getIntInput("Enter item number to delete: ") - 1;

        if (index >= 0 && index < items.size()) {
            items.remove(index);
            System.out.println("✓ Item deleted successfully!");
        } else {
            System.out.println("Invalid item number.");
        }
    }

    // Delivery Management
    private static void manageDeliveries() {
        boolean back = false;
        while (!back) {
            printHeader("DELIVERY MANAGEMENT");
            System.out.println("1. Add Delivery");
            System.out.println("2. View All Deliveries");
            System.out.println("3. Update Delivery");
            System.out.println("4. Delete Delivery");
            System.out.println("5. Back");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1 -> addDelivery();
                case 2 -> viewDeliveries();
                case 3 -> updateDelivery();
                case 4 -> deleteDelivery();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addDelivery() {
        Delivers d = Delivers.inputDelivery();      
        deliveries.add(d);                     
        d.insertToDatabase_Delivers();        
        System.out.println("Delivery added successfully!");
    }

    private static void viewDeliveries() {
        Delivers.viewDeliveries();
    }

    private static void updateDelivery() {
        if (deliveries.isEmpty()) {
            System.out.println("No deliveries to update.");
            return;
        }
        viewDeliveries();
        int index = getIntInput("Enter delivery number to update: ") - 1;

        if (index >= 0 && index < deliveries.size()) {
            deliveries.get(index).updateInfo(sc);
        } else {
            System.out.println("Invalid delivery number.");
        }
    }

    private static void deleteDelivery() {
        if (deliveries.isEmpty()) {
            System.out.println("No deliveries to delete.");
            return;
        }
        viewDeliveries();
        int index = getIntInput("Enter delivery number to delete: ") - 1;

        if (index >= 0 && index < deliveries.size()) {
            deliveries.remove(index);
            System.out.println("✓ Delivery deleted successfully!");
        } else {
            System.out.println("Invalid delivery number.");
        }
    }

    private static void displayAllRecords() {
        printHeader("ALL RECORDS");

        System.out.println("\n--- ITEMS ---");
        viewItems();

         System.out.println("\n--- SUPPLIERS ---");
        viewSuppliers();
        
        System.out.println("\n--- DELIVERIES ---");
        viewDeliveries();

        System.out.println("\n========================================");
    }
}