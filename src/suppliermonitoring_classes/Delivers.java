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

    // --- INPUT METHOD ---
    public static Delivers inputDelivery() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- ADD DELIVERY ---");

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

        // deliveryID is auto-generated in database
        return new Delivers(null, supplierID, itemID, consignor, supplierName, itemName, quantity, price, date);
    }

    // --- DISPLAY METHOD ---
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

    // --- UPDATE METHOD ---
    public void updateInfo(Scanner sc) {
        System.out.println("\n--- UPDATE DELIVERY ---");
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

    // --- DATABASE INSERTION ---
    public void insertToDatabase_Delivers() {
        String insertQuery = "INSERT INTO delivers (supplierID, itemID, consignor, supplier_name, item_name, quantity, price, date) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            String supplierCheck = "SELECT COUNT(*) FROM supplier WHERE supplierID = ?";
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

            // 3️⃣ Insert delivery and retrieve auto-generated ID
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
    
    public static void viewDeliveries() {
    String query = "SELECT * FROM delivers";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("\n--- DELIVERIES IN DATABASE ---");

        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;

            String deliveryID = rs.getString("deliveryID");
            String supplierID = rs.getString("supplierID");
            String itemID = rs.getString("itemID");
            String consignor = rs.getString("consignor");
            String supplierName = rs.getString("supplier_name");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            double price = rs.getDouble("price");
            String date = rs.getString("date");

            System.out.println("\nDelivery ID: " + deliveryID);
            System.out.println("Supplier ID: " + supplierID);
            System.out.println("Item ID: " + itemID);
            System.out.println("Consignor: " + consignor);
            System.out.println("Supplier Name: " + supplierName);
            System.out.println("Item Name: " + itemName);
            System.out.println("Quantity: " + quantity);
            System.out.println("Price: " + price);
            System.out.println("Date: " + date);
        }

        if (!hasResults) {
            System.out.println("No deliveries found in the database.");
        }

    } catch (SQLException e) {
        System.err.println("Error retrieving deliveries: " + e.getMessage());
        }
    }
}