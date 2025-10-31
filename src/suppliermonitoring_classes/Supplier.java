package suppliermonitoring_classes;

import java.util.Scanner;
import java.sql.*;

public class Supplier {
    private String supplierID;   // auto_increment — no need to input manually
    private String supplierName;
    private String address;
    private String phoneNumber;

    public Supplier(String supplierID, String supplierName, String address, String phoneNumber) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    public static Supplier inputSupplier() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- ADD SUPPLIER ---");
        System.out.print("Supplier Name: ");
        String name = sc.nextLine();
        System.out.print("Address: ");
        String address = sc.nextLine();
        System.out.print("Phone Number: ");
        String phone = sc.nextLine();
        return new Supplier(null, name, address, phone);
    }

    public void displayInfo() {
        System.out.println("Supplier ID: " + supplierID);
        System.out.println("Supplier Name: " + supplierName);
        System.out.println("Address: " + address);
        System.out.println("Phone Number: " + phoneNumber);
    }

    
    public void updateInfo(Scanner sc) {
        System.out.println("\n--- UPDATE SUPPLIER ---");
        System.out.println("Leave blank to keep current value.");

        System.out.print("New Name [" + supplierName + "]: ");
        String newName = sc.nextLine();
        if (!newName.isEmpty()) supplierName = newName;

        System.out.print("New Address [" + address + "]: ");
        String newAddress = sc.nextLine();
        if (!newAddress.isEmpty()) address = newAddress;

        System.out.print("New Phone Number [" + phoneNumber + "]: ");
        String newPhone = sc.nextLine();
        if (!newPhone.isEmpty()) phoneNumber = newPhone;

        System.out.println("Supplier updated successfully!");
    }

    public void insertToDatabase() {
        // supplierID is auto_increment, so we don’t include it in the query
        String query = "INSERT INTO supplier (full_name, address, phone_num) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, supplierName);
            pstmt.setString(2, address);
            pstmt.setString(3, phoneNumber);

            pstmt.executeUpdate();
            System.out.println("Supplier successfully inserted into database.");

        } catch (SQLException e) {
            System.err.println("Error inserting supplier: " + e.getMessage());
        }
    } 
    
    // purpose: to retrieve all suppliers information
    public static void viewSuppliers() {
    String query = "SELECT * FROM supplier";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("\n--- SUPPLIERS IN DATABASE ---");

        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;

            String supplierID = rs.getString("supplierID");
            String fullName = rs.getString("full_name");
            String address = rs.getString("address");
            String phone = rs.getString("phone_num");

            System.out.println("\nSupplier ID: " + supplierID);
            System.out.println("Supplier Name: " + fullName);
            System.out.println("Address: " + address);
            System.out.println("Phone Number: " + phone);
        }

        if (!hasResults) {
            System.out.println("No suppliers found in the database.");
        }

    } catch (SQLException e) {
        System.err.println("Error retrieving suppliers: " + e.getMessage());
        }
    }
}




