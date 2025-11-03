package suppliermonitoring_classes;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import java.util.Arrays; 

public class Main {

    private static final ArrayList<Supplier> suppliers = new ArrayList<>();
    private static final ArrayList<Items> items = new ArrayList<>();
    private static final ArrayList<Delivers> deliveries = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) {
        DatabaseConnection.getConnection(); 

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
        DatabaseConnection.closeConnection(); 
        sc.close();
    }
    
    private static void printHeader(String title) {
        System.out.println("\n========================================");
        System.out.println("        " + title);
        System.out.println("========================================");
    }
    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
    
    private static String getStringInput(String prompt, boolean allowEmpty) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = sc.nextLine().trim();
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

  
    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid decimal number (e.g., 99.99).");
            }
        }
    }
    
    private static void printTable(String title, Vector<Vector<Object>> data, Vector<String> columnNames) {
        System.out.println("\n--- " + title + " ---");
        if (data.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        int[] widths = new int[columnNames.size()];
        for (int i = 0; i < columnNames.size(); i++) {
            widths[i] = columnNames.get(i).length();
        }
        for (Vector<Object> row : data) {
            for (int i = 0; i < row.size(); i++) {
                widths[i] = Math.max(widths[i], row.get(i).toString().length());
            }
        }

        StringBuilder header = new StringBuilder("|");
        StringBuilder separator = new StringBuilder("+");
        for (int i = 0; i < columnNames.size(); i++) {
            header.append(String.format(" %-" + widths[i] + "s |", columnNames.get(i)));
            separator.append("-".repeat(widths[i] + 2)).append("+");
        }

        System.out.println(separator);
        System.out.println(header);
        System.out.println(separator);

        for (Vector<Object> row : data) {
            StringBuilder rowStr = new StringBuilder("|");
            for (int i = 0; i < row.size(); i++) {
                rowStr.append(String.format(" %-" + widths[i] + "s |", row.get(i).toString()));
            }
            System.out.println(rowStr);
        }
        System.out.println(separator);
    }

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
        printHeader("ADD NEW SUPPLIER");
        String name = getStringInput("Enter Supplier Name: ", false);
        String address = getStringInput("Enter Address: ", false);
        String phone = getStringInput("Enter Phone Number: ", false);

        Supplier s = new Supplier(null, name, address, phone); // Changed constructor call based on DAO
        String result = s.insertToDatabase();
        System.out.println(result);
    }

    private static void viewSuppliers() {
        Vector<Vector<Object>> data = Supplier.getAllSuppliersForTable();
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Name", "Address", "Phone"));
        printTable("All Suppliers", data, headers);
    }

    private static void updateSupplier() {
        printHeader("UPDATE SUPPLIER");
        String id = getStringInput("Enter Supplier ID to update: ", false);
        
        System.out.println("Enter new details (leave empty to keep current value):");
        String newName = getStringInput("New Name: ", true);
        String newAddress = getStringInput("New Address: ", true);
        String newPhone = getStringInput("New Phone Number: ", true);

        String result = Supplier.updateSupplierInDatabase(id, newName, newAddress, newPhone);
        System.out.println(result);
    }

    private static void deleteSupplier() {
        printHeader("DELETE SUPPLIER");
        String id = getStringInput("Enter Supplier ID to delete: ", false);
        String result = Supplier.deleteSupplierFromDatabase(id);
        System.out.println(result);
    }

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
        printHeader("ADD NEW ITEM");
        String code = getStringInput("Enter Item Code: ", false); 

        String name = getStringInput("Enter Item Name: ", false);
        String category = getStringInput("Enter Item Category: ", false);
        Items i = new Items(null, code, name, category); 
        String result = i.insertToDatabase_Item();
        System.out.println(result);
    }
    private static void viewItems() {
        Vector<Vector<Object>> data = Items.getAllItemsForTable();
        Vector<String> headers = new Vector<>(Arrays.asList("ID", "Code", "Name", "Category"));
        printTable("All Items", data, headers);
    }
    
    private static void updateItem() {
        printHeader("UPDATE ITEM");
        String id = getStringInput("Enter Item ID to update: ", false);
        
        System.out.println("Enter new details (leave empty to keep current value):");
        String newCode = getStringInput("New Code: ", true); 
        String newName = getStringInput("New Name: ", true);
        String newCategory = getStringInput("New Category: ", true);

        String result = Items.updateItemInDatabase(id, newCode, newName, newCategory);
        System.out.println(result);
    }
    
    private static void deleteItem() {
        printHeader("DELETE ITEM");
        String id = getStringInput("Enter Item ID to delete: ", false);
        String result = Items.deleteItemFromDatabase(id);
        System.out.println(result);
    }
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
        printHeader("ADD NEW DELIVERY");
        String supplierID = getStringInput("Enter Supplier ID (must exist): ", false);
        String itemID = getStringInput("Enter Item ID (must exist): ", false);
        String consignor = getStringInput("Enter Consignor Name: ", false);
        String supplierName = getStringInput("Enter Supplier Name (for record): ", false);
        String itemName = getStringInput("Enter Item Name (for record): ", false);
        int quantity = getIntInput("Enter Quantity: ");
        double price = getDoubleInput("Enter Price: ");
        String date = getStringInput("Enter Date (YYYY-MM-DD): ", false);
        
        // deliveryID is null on creation, DB handles generation.
        Delivers d = new Delivers(null, supplierID, itemID, consignor, supplierName, itemName, quantity, price, date); 
        String result = d.insertToDatabase_Delivers();
        System.out.println(result);
    }
    private static void viewDeliveries() {
        Vector<Vector<Object>> data = Delivers.getAllDeliveriesForTable();
        Vector<String> headers = new Vector<>(Arrays.asList("Delivery ID", "Supplier ID", "Item ID", "Consignor", "Supplier Name", "Item Name", "Qty", "Price", "Date"));
        printTable("All Deliveries", data, headers);
    }

    private static void updateDelivery() {
        printHeader("UPDATE DELIVERY");
        String id = getStringInput("Enter Delivery ID to update: ", false);
        
        System.out.println("Enter new details (leave empty/0/-1.0 to keep current value):");
        int newQuantity = getIntInput("New Quantity (Enter -1 to skip): ");
        double newPrice = getDoubleInput("New Price (Enter -1.0 to skip): ");
        String newDate = getStringInput("New Date (YYYY-MM-DD) (leave empty to skip): ", true);

        String result = Delivers.updateDeliveryInDatabase(id, newQuantity, newPrice, newDate);
        System.out.println(result);
    }
    private static void deleteDelivery() {
        printHeader("DELETE DELIVERY");
        String id = getStringInput("Enter Delivery ID to delete: ", false);
        String result = Delivers.deleteDeliveryFromDatabase(id);
        System.out.println(result);
    }

    private static void displayAllRecords() {
        printHeader("ALL RECORDS");

        viewItems();
        viewSuppliers();
        viewDeliveries();

        System.out.println("\n========================================");
    }
}