package suppliermonitoring_classes;

import java.util.Scanner;
import java.sql.*;

public class Delivers {
    private String deliveryID;
    private String supplierID;
    private String itemID;
    private String consignor;
    private String supplierName;
    private String itemName;
    private int quantity;
    private double price;
    private String date;

    // constructor to initialize delivery object with all field values
    public Delivers(String deliveryID, String supplierID, String itemID, String consignor,
                    String supplierName, String itemName, int quantity, double price, String date) {
        this.deliveryID = deliveryID;
        this.supplierID = supplierID;
        this.itemID = itemID;
        this.consignor = consignor;
        this.supplierName = supplierName;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }
    
    // to ask user for new delivery details and used when adding a new record (database)
    public static Delivers inputDelivery() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nADD DELIVERY");

        System.out.print("Supplier ID: ");
        String supplierID = sc.nextLine();

        System.out.print("Item ID: ");
        String itemID = sc.nextLine();

        System.out.print("Consignor: ");
        String consignor = sc.nextLine();

        System.out.print("Supplier Name: ");
        String supplierName = sc.nextLine();

        System.out.print("Item Name: ");
        String itemName = sc.nextLine();

        int quantity = getIntInput(sc, "Quantity: ");
        double price = getDoubleInput(sc, "Price: ");

        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();

        return new Delivers(null, supplierID, itemID, consignor, supplierName, itemName, quantity, price, date);
    }

    // to display all info
    public void displayInfo() {
        System.out.println("Delivery ID: " + deliveryID);
        System.out.println("Supplier ID: " + supplierID);
        System.out.println("Item ID: " + itemID);
        System.out.println("Consignor: " + consignor);
        System.out.println("Supplier Name: " + supplierName);
        System.out.println("Item Name: " + itemName);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);
        System.out.println("Date: " + date);
    }

    // purpose: update old data 
    public void updateInfo(Scanner sc) {
        System.out.println("\nUPDATE DELIVERY");
        System.out.println("Leave blank to keep current value.");

        System.out.print("New Quantity [" + quantity + "]: ");
        String newQty = sc.nextLine();
        if (!newQty.isEmpty()) {
            try { quantity = Integer.parseInt(newQty); } 
            catch (NumberFormatException e) { System.out.println("Invalid number."); }
        }

        System.out.print("New Price [" + price + "]: ");
        String newPrice = sc.nextLine();
        if (!newPrice.isEmpty()) {
            try { price = Double.parseDouble(newPrice); } 
            catch (NumberFormatException e) { System.out.println("Invalid number."); }
        }

        System.out.print("New Date [" + date + "]: ");
        String newDate = sc.nextLine();
        if (!newDate.isEmpty()) date = newDate;

        System.out.println("✓ Delivery updated successfully!");
    }

    // purpose: insert new data in delivers method (includes validation that supplierID and itemID exists)
    public void insertToDatabase_Delivers() {
        String insertQuery = "INSERT INTO delivers (supplierID, itemID, consignor, supplier_name, item_name, quantity, price, date) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            String supplierCheck = "SELECT COUNT(*) FROM supplier WHERE supplierID = ?";
            // to check if supplierID exist
            try (PreparedStatement pstmt = conn.prepareStatement(supplierCheck)) {
                pstmt.setString(1, supplierID);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.err.println("Error: Supplier ID '" + supplierID + "' does not exist in supplier table.");
                    return;
                }
            }

            String itemCheck = "SELECT COUNT(*) FROM item WHERE itemID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(itemCheck)) {
                pstmt.setString(1, itemID);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    System.err.println("Error: Item ID '" + itemID + "' does not exist in item table.");
                    return;
                }
            }
            
            // verification and insert record into delivers table
            try (PreparedStatement pstmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, supplierID);
                pstmt.setString(2, itemID);
                pstmt.setString(3, consignor);
                pstmt.setString(4, supplierName);
                pstmt.setString(5, itemName);
                pstmt.setInt(6, quantity);
                pstmt.setDouble(7, price);
                pstmt.setString(8, date);

                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int generatedID = rs.getInt(1);
                    System.out.println("Delivery successfully inserted! Generated Delivery ID: " + generatedID);
                } else {
                    System.out.println("Delivery successfully inserted!");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error inserting delivery: " + e.getMessage());
        }
    }

    // purpose: if you select it all from delivers table, it will show all the data
    public static void viewDeliveries() {
        String query = "SELECT * FROM delivers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nDELIVERIES IN DATABASE");

            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;

                System.out.println("\nDelivery ID: " + rs.getString("deliveryID"));
                System.out.println("Supplier ID: " + rs.getString("supplierID"));
                System.out.println("Item ID: " + rs.getString("itemID"));
                System.out.println("Consignor: " + rs.getString("consignor"));
                System.out.println("Supplier Name: " + rs.getString("supplier_name"));
                System.out.println("Item Name: " + rs.getString("item_name"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Price: " + rs.getDouble("price"));
                System.out.println("Date: " + rs.getString("date"));
            }

            if (!hasResults) {
                System.out.println("No deliveries found in the database.");
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving deliveries: " + e.getMessage());
        }
    }
    
    // purpose: it also updates the data in sql
    public static void updateDeliveryInDatabase() {
        Scanner sc = new Scanner(System.in);
        viewDeliveries();

        System.out.print("\nEnter Delivery ID to update: ");
        String deliveryID = sc.nextLine();

        String selectQuery = "SELECT * FROM delivers WHERE deliveryID = ?";
        String updateQuery = "UPDATE delivers SET quantity = ?, price = ?, date = ? WHERE deliveryID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            // fetch current record
            selectStmt.setString(1, deliveryID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentQuantity = rs.getInt("quantity");
                double currentPrice = rs.getDouble("price");
                String currentDate = rs.getString("date");

                System.out.println("UPDATE DELIVERY");
                System.out.println("Leave blank to keep current value.");

                System.out.print("New Quantity [" + currentQuantity + "]: ");
                String newQtyInput = sc.nextLine();
                int newQuantity = newQtyInput.isEmpty() ? currentQuantity : Integer.parseInt(newQtyInput);

                System.out.print("New Price [" + currentPrice + "]: ");
                String newPriceInput = sc.nextLine();
                double newPrice = newPriceInput.isEmpty() ? currentPrice : Double.parseDouble(newPriceInput);

                System.out.print("New Date [" + currentDate + "]: ");
                String newDate = sc.nextLine();
                if (newDate.isEmpty()) newDate = currentDate;

                // perform update
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, newQuantity);
                    updateStmt.setDouble(2, newPrice);
                    updateStmt.setString(3, newDate);
                    updateStmt.setString(4, deliveryID);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Delivery successfully updated in database!");
                    } else {
                        System.out.println("Delivery not found or not updated.");
                    }
                }
            } else {
                System.out.println("No delivery found with ID: " + deliveryID);
            }

        } catch (SQLException e) {
            System.err.println("Error updating delivery: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid input. Please enter valid numbers for quantity and price.");
        }
    }
    
    // to delete a delivery record from the database
    public static void deleteDeliveryFromDatabase() {
        Scanner sc = new Scanner(System.in);
        viewDeliveries();

        System.out.print("\nEnter Delivery ID to delete: ");
        String deliveryID = sc.nextLine();

        String deleteQuery = "DELETE FROM deliveries WHERE deliveryID = ?";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

        pstmt.setString(1, deliveryID);
        int rows = pstmt.executeUpdate();

        if (rows > 0) {
            System.out.println("✓ Delivery deleted successfully from database!");
        } else {
            System.out.println("No delivery found with that ID.");
        }

    } catch (SQLException e) {
        System.err.println("Error deleting delivery: " + e.getMessage());
    }
}

    // error handlings (safely read integer input)
    public static int getIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    
    public static double getDoubleInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }
}
