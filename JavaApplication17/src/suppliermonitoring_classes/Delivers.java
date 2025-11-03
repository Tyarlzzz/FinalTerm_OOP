package suppliermonitoring_classes;

import java.sql.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;

/**
 * Model class representing a Delivery record.
 * This class is used to hold data retrieved from the Deliveries table,
 * including joined fields (supplierName, itemName) for display in the GUI.
 */
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

    // Assuming DatabaseConnection class exists and has a static getConnection() method.

    /**
     * Constructor to initialize delivery object. Used for both creation and reading.
     */
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

    // --- Getter Methods ---
    public String getDeliveryID() { return deliveryID; }
    public String getSupplierID() { return supplierID; }
    public String getItemID() { return itemID; }
    public String getConsignor() { return consignor; }
    public String getSupplierName() { return supplierName; }
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getDate() { return date; }

    // --- Setter Methods ---
    public void setDeliveryID(String deliveryID) { this.deliveryID = deliveryID; }
    public void setSupplierID(String supplierID) { this.supplierID = supplierID; }
    public void setItemID(String itemID) { this.itemID = itemID; }
    public void setConsignor(String consignor) { this.consignor = consignor; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setDate(String date) { this.date = date; }
    // The duplicate get methods were removed here.


    public String insertToDatabase_Delivers() {
        String insertQuery = "INSERT INTO delivers (supplierID, itemID, consignor, supplier_name, item_name, quantity, price, date) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Check if supplierID exists
            String supplierCheck = "SELECT COUNT(*) FROM supplier WHERE supplierID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(supplierCheck)) {
                pstmt.setString(1, supplierID);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    return "❌ Error: Supplier ID '" + supplierID + "' does not exist in the database.";
                }
            }

            // 2. Check if itemID exists
            String itemCheck = "SELECT COUNT(*) FROM item WHERE itemID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(itemCheck)) {
                pstmt.setString(1, itemID);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    return "❌ Error: Item ID '" + itemID + "' does not exist in the database.";
                }
            }
            
            // 3. Insert the delivery record
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
                    // Update the object's deliveryID property and return success message
                    this.deliveryID = rs.getString(1);
                    // Fixed syntax error (extra parenthesis removed)
                    return ("✓ Delivery successfully inserted! ID: " + this.deliveryID);
                } else {
                    return "✓ Delivery successfully inserted!";
                }
            }

        } catch (SQLException e) {
            return "❌ Error inserting delivery: " + e.getMessage();
        }
    }

    /**
     * Retrieves all deliveries and formats the data for a JTable model.
     * @return Vector of Vectors containing all delivery data.
     */
    public static Vector<Vector<Object>> getAllDeliveriesForTable() {
        String query = "SELECT deliveryID, supplierID, itemID, consignor, supplier_name, item_name, quantity, price, date FROM delivers";
        
        // JTable/Swing compatibility requires Vector<Vector<Object>>
        Vector<Vector<Object>> tableData = new Vector<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                // Simplified logic to use only Vector for JTable compatibility
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("deliveryID"));
                row.add(rs.getString("supplierID"));
                row.add(rs.getString("itemID"));
                row.add(rs.getString("consignor"));
                row.add(rs.getString("supplier_name"));
                row.add(rs.getString("item_name"));
                row.add(rs.getInt("quantity"));
                row.add(rs.getDouble("price"));
                row.add(rs.getString("date"));
                
                tableData.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving deliveries for table: " + e.getMessage());
        }
        
        return tableData;
    }

    /**
     * Updates an existing delivery record in the database.
     * @param deliveryID The ID of the delivery to update.
     * @param newQuantity The new quantity (or -1 to ignore).
     * @param newPrice The new price (or -1.0 to ignore).
     * @param newDate The new date (or empty string to ignore).
     * @return A status message.
     */
    public static String updateDeliveryInDatabase(String deliveryID, int newQuantity, double newPrice, String newDate) {
        String selectQuery = "SELECT quantity, price, date FROM delivers WHERE deliveryID = ?";
        String updateQuery = "UPDATE delivers SET quantity = ?, price = ?, date = ? WHERE deliveryID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, deliveryID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                // Get current values
                int currentQuantity = rs.getInt("quantity");
                double currentPrice = rs.getDouble("price");
                String currentDate = rs.getString("date");

                // Determine final values (using sentinel values: -1 for int/double, empty for string)
                int finalQuantity = (newQuantity == -1) ? currentQuantity : newQuantity;
                double finalPrice = (newPrice == -1.0) ? currentPrice : newPrice;
                String finalDate = newDate.isEmpty() ? currentDate : newDate;

                // Perform update
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, finalQuantity);
                    updateStmt.setDouble(2, finalPrice);
                    updateStmt.setString(3, finalDate);
                    updateStmt.setString(4, deliveryID);

                    int rows = updateStmt.executeUpdate();
                    if (rows > 0) {
                        return "✓ Delivery ID " + deliveryID + " successfully updated.";
                    } else {
                        return "⚠️ Delivery not found or no changes made.";
                    }
                }
            } else {
                return "❌ No delivery found with ID: " + deliveryID;
            }

        } catch (SQLException e) {
            return "❌ Error updating delivery: " + e.getMessage();
        }
    }

    /**
     * Deletes a delivery record from the database.
     * @param deliveryID The ID of the delivery to delete.
     * @return A status message.
     */
    public static String deleteDeliveryFromDatabase(String deliveryID) {
        String deleteQuery = "DELETE FROM delivers WHERE deliveryID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {

            pstmt.setString(1, deliveryID);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                return "✓ Delivery ID " + deliveryID + " deleted successfully.";
            } else {
                return "❌ No delivery found with ID: " + deliveryID;
            }

        } catch (SQLException e) {
            return "❌ Error deleting delivery: " + e.getMessage();
        }
    }
}
