package suppliermonitoring_classes;

import java.sql.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

public class Supplier {
    private final String supplierID;    // auto_increment — read-only after creation
    private final String supplierName;
    private final String address;
    private final String phoneNumber;

    /**
     * Full constructor used when fetching from the database.
     * @param supplierID
     * @param supplierName
     * @param address
     * @param phoneNumber
     */
    public Supplier(String supplierID, String supplierName, String address, String phoneNumber) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
<<<<<<< Updated upstream

    /**
     * Constructor used when creating a new supplier for insertion (ID is null).
     * @param supplierName
     * @param address
     * @param phoneNumber
     */
    public Supplier(String supplierName, String address, String phoneNumber) {
=======
public Supplier(String supplierName, String address, String phoneNumber) {
>>>>>>> Stashed changes
        this(null, supplierName, address, phoneNumber);
    }

    // --- Getters for GUI binding ---
    public String getSupplierID() { return supplierID; }
    public String getSupplierName() { return supplierName; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }


<<<<<<< Updated upstream
    
    public String insertToDatabase() {
        // We do not insert supplierID as it is auto-incremented by the database.
=======
    /**
     * Inserts the current Supplier object's data into the database.
     * @return A status message (success or error).
     */
    public String insertToDatabase() {
>>>>>>> Stashed changes
        String query = "INSERT INTO supplier (full_name, address, phone_num) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, supplierName);
            pstmt.setString(2, address);
            pstmt.setString(3, phoneNumber);
            pstmt.executeUpdate();

            return "✓ Supplier '" + supplierName + "' successfully added.";
        } catch (SQLException e) {
            return "❌ Error inserting supplier: " + e.getMessage();
        }
    }

<<<<<<< Updated upstream
   
    public static Vector<Vector<Object>> getAllSuppliersForTable() {
        String query = "SELECT supplierID, full_name, address, phone_num FROM supplier";
        
      
        List<List<Object>> listData = new ArrayList<>(); 
=======
    /**
     * Retrieves all suppliers and formats the data for a JTable model.
     * @return Vector of Vectors containing all supplier data.
     */
    public static Vector<Vector<Object>> getAllSuppliersForTable() {
        String query = "SELECT supplierID, full_name, address, phone_num FROM supplier";
        Vector<Vector<Object>> tableData = new Vector<>();
>>>>>>> Stashed changes

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
<<<<<<< Updated upstream
                List<Object> row = new ArrayList<>();
=======
                Vector<Object> row = new Vector<>();
>>>>>>> Stashed changes
                row.add(rs.getString("supplierID"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("address"));
                row.add(rs.getString("phone_num"));
<<<<<<< Updated upstream
                listData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving suppliers for table: " + e.getMessage());
        }
        
        // Convert the internal List structure back to the required Vector structure
        Vector<Vector<Object>> tableData = new Vector<>();
        for (List<Object> listRow : listData) {
            tableData.add(new Vector<>(listRow)); 
        }

=======
                tableData.add(row);
            }
        } catch (SQLException e) {
            // In a GUI, an empty list might be returned, and the error logged or shown in a dialog
            System.err.println("Error retrieving suppliers for table: " + e.getMessage());
        }
>>>>>>> Stashed changes
        return tableData;
    }

    /**
<<<<<<< Updated upstream
     * Updates an existing supplier record in the database.If a new parameter is empty, the existing value is retained.
     * * @param supplierID The ID of the supplier to update.
     * @param supplierID
     * @param newName The new name (or empty string to keep current).
     * @param newAddress The new address (or empty string to keep current).
     * @param newPhone The new phone number (or empty string to keep current).
     * @return A status message.
     */
    public static String updateSupplierInDatabase(String supplierID, String newName, String newAddress, String newPhone) {
=======
     * Updates an existing supplier record in the database.
     * @param supplierID The ID of the supplier to update.
     * @param newName The new name (must be a valid string, not empty).
     * @param newAddress The new address.
     * @param newPhone The new phone number.
     * @return A status message.
     */
    public static String updateSupplierInDatabase(String supplierID, String newName, String newAddress, String newPhone) {
        // 1. Fetch current data to handle blank inputs from GUI
>>>>>>> Stashed changes
        String selectQuery = "SELECT full_name, address, phone_num FROM supplier WHERE supplierID = ?";
        String updateQuery = "UPDATE supplier SET full_name = ?, address = ?, phone_num = ? WHERE supplierID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, supplierID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
<<<<<<< Updated upstream
               
=======
                // Determine final values (use new input if provided, otherwise keep current DB value)
>>>>>>> Stashed changes
                String finalName = newName.isEmpty() ? rs.getString("full_name") : newName;
                String finalAddress = newAddress.isEmpty() ? rs.getString("address") : newAddress;
                String finalPhone = newPhone.isEmpty() ? rs.getString("phone_num") : newPhone;

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setString(1, finalName);
                    updateStmt.setString(2, finalAddress);
                    updateStmt.setString(3, finalPhone);
                    updateStmt.setString(4, supplierID);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        return "✓ Supplier ID " + supplierID + " successfully updated.";
                    } else {
<<<<<<< Updated upstream
                       
                        return "⚠️ Supplier not found or no changes made."; 
=======
                        return "⚠️ Supplier not found or no changes made.";
>>>>>>> Stashed changes
                    }
                }
            } else {
                return "❌ No supplier found with ID: " + supplierID;
            }

        } catch (SQLException e) {
            return "❌ Error updating supplier: " + e.getMessage();
        }
    }

    /**
     * Deletes a supplier record from the database.
     * @param supplierID The ID of the supplier to delete.
     * @return A status message.
     */
    public static String deleteSupplierFromDatabase(String supplierID) {
        String deleteQuery = "DELETE FROM supplier WHERE supplierID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            deleteStmt.setString(1, supplierID);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                return "✓ Supplier ID " + supplierID + " successfully deleted.";
            } else {
                return "❌ Supplier ID " + supplierID + " not found.";
            }

        } catch (SQLException e) {
<<<<<<< Updated upstream
            // Catches foreign key constraint errors if this supplier is still linked to a delivery
            if (e.getErrorCode() == 1451) { // MySQL error code for foreign key constraint failure
                return "❌ Cannot delete supplier: Still linked to one or more deliveries.";
            }
=======
>>>>>>> Stashed changes
            return "❌ Error deleting supplier: " + e.getMessage();
        }
    }
}
