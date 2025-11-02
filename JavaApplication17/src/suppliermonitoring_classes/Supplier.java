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

    // input new data from supplier
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

    // display all infor
    public void displayInfo() {
        System.out.println("Supplier ID: " + supplierID);
        System.out.println("Supplier Name: " + supplierName);
        System.out.println("Address: " + address);
        System.out.println("Phone Number: " + phoneNumber);
    }

    // update supplier details
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

    // purpose: to insert new data inside database
    public void insertToDatabase() {
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

    // purpose: if you select it all from supplier table, it will show all the data
    public static void viewSuppliers() {
        String query = "SELECT * FROM supplier";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("SUPPLIERS IN DATABASE");

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

    // purpose: to update existing data inside database
    public static void updateSupplierInDatabase() {
        Scanner sc = new Scanner(System.in);
        viewSuppliers();

        System.out.print("\nEnter Supplier ID to update: ");
        String supplierID = sc.nextLine();

        String selectQuery = "SELECT * FROM supplier WHERE supplierID = ?"; 
        String updateQuery = "UPDATE supplier SET full_name = ?, address = ?, phone_num = ? WHERE supplierID = ?";
        
        //connection in databaseconnection.java
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, supplierID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                String currentName = rs.getString("full_name");
                String currentAddress = rs.getString("address");
                String currentPhone = rs.getString("phone_num");

                System.out.println("UPDATE SUPPLIER");
                System.out.println("Leave blank to keep current value.");

                System.out.print("New Name [" + currentName + "]: ");
                String newName = sc.nextLine();
                if (newName.isEmpty()) newName = currentName;

                System.out.print("New Address [" + currentAddress + "]: ");
                String newAddress = sc.nextLine();
                if (newAddress.isEmpty()) newAddress = currentAddress;

                System.out.print("New Phone Number [" + currentPhone + "]: ");
                String newPhone = sc.nextLine();
                if (newPhone.isEmpty()) newPhone = currentPhone;

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, newName);
                    updateStmt.setString(2, newAddress);
                    updateStmt.setString(3, newPhone);
                    updateStmt.setString(4, supplierID);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        System.out.println("Supplier successfully updated in the database!");
                    } else {
                        System.out.println("Supplier not found or not updated.");
                    }
                }

            } else {
                System.out.println("No supplier found with ID: " + supplierID);
            }

        } catch (SQLException e) {
            System.err.println("Error updating supplier: " + e.getMessage());
        }
    }
    
    // purpose: delete a supplier from the database
    public static void deleteSupplierFromDatabase() {
        Scanner sc = new Scanner(System.in);
        viewSuppliers(); // show all suppliers first

        System.out.print("\nEnter the Supplier ID you want to delete: ");
        String supplierID = sc.nextLine();

        String checkQuery = "SELECT * FROM supplier WHERE supplierID = ?"; // a query for database 
        String deleteQuery = "DELETE FROM supplier WHERE supplierID = ?";

    // connection in database connection.java
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {

        checkStmt.setString(1, supplierID);
        ResultSet rs = checkStmt.executeQuery();

        if (!rs.next()) {
            System.out.println("Supplier ID not found in the database.");
            return;
        }

        // confirm deletion
        System.out.println("\nSupplier to delete:");
        System.out.println("Name: " + rs.getString("full_name"));
        System.out.println("Address: " + rs.getString("address"));
        System.out.println("Phone: " + rs.getString("phone_num"));
        System.out.print("\nAre you sure you want to delete this supplier? (y/n): ");
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Deletion cancelled.");
            return;
        }

        // proceed with deletion
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
            deleteStmt.setString(1, supplierID);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("Supplier successfully deleted from the database!");
            } else {
                System.out.println("No supplier deleted. Please check the Supplier ID.");
            }
        }

    } catch (SQLException e) {
        System.err.println("Error deleting supplier: " + e.getMessage());
        }
    }
}