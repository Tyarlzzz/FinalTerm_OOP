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

        // switch case for choosing options (to choose which module to manage)
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
    
    // purpose: to print a section header (for console only)
    private static void printHeader(String title) {
        System.out.println("========================================");
        System.out.println("      " + title);
        System.out.println("========================================");
    }

    // error handling (it validates user input to ensure number lang ang pwede
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

    // switch case for choosing options (add, view, update, delete)
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

    // calling the method to insert new data in database
    private static void addSupplier() {
        Supplier s = Supplier.inputSupplier();
        suppliers.add(s);
        s.insertToDatabase(); 
        System.out.println("Supplier added successfully!");
    }

    private static void viewSuppliers() {
        Supplier.viewSuppliers(); // calling the method from supplier class to view data from our database
    }

    private static void updateSupplier() {
       Supplier.updateSupplierInDatabase(); // calling the method from supplier class to update old data from our database
    }

    private static void deleteSupplier() {
        Supplier.deleteSupplierFromDatabase(); // calling the method from supplier class to delete data from our database
    }
    
    // this method handles item management option 
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

    // calling the method to insert new data into database
    private static void addItem() {
        Items i = Items.inputItem();      
        items.add(i);                     
        i.insertToDatabase_Item();        
        System.out.println("Item/s added successfully!");
    }

    // calling the method to view data sa loob ng items
    private static void viewItems() {
         Items.viewItems();
    }
    
    // calling the method to update old data inside the database
    private static void updateItem() {
        Items.updateItemInDatabase();
    }
    
    // calling the method to delete an item from database
    private static void deleteItem() {
      Items.deleteItemFromDatabase();
    }

    // handles delivery management options
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

    // insert new data into our database
    private static void addDelivery() {
        Delivers d = Delivers.inputDelivery();      
        deliveries.add(d);                     
        d.insertToDatabase_Delivers();        
        System.out.println("Delivery added successfully!");
    }

    // view all deliveries from the database
    private static void viewDeliveries() {
        Delivers.viewDeliveries();
    }

    // updates old delivery data inside the database
    private static void updateDelivery() {
       Delivers.updateDeliveryInDatabase();
    }
    
    //deletes a delivery record from the database
    private static void deleteDelivery() {
        Delivers.updateDeliveryInDatabase();
    }

    private static void displayAllRecords() {
        printHeader("ALL RECORDS");

        System.out.println("\nITEMS");
        viewItems();

         System.out.println("\nSUPPLIERS");
        viewSuppliers();
        
        System.out.println("\nDELIVERIES");
        viewDeliveries();

        System.out.println("\n========================================");
    }
}