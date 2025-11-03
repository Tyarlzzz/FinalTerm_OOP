package suppliermonitoring_classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import javax.swing.JOptionPane;

public class Items {
    // We remove 'final' from itemID so that we can instantiate it with null (for insert)
    // and correctly represent data loaded from the database.
    private String itemID; 
    private final String code; 
    private final String name;
    private final String category;

    // Constructor used when creating a NEW item (itemID is null) or loading from DB
    public Items(String itemID, String code, String name, String category) {
        this.itemID = itemID;
        this.code = code;
        this.name = name;
        this.category = category;
    }

    // Getters
    public String getItemID() { return itemID; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }

    // ------------------------------------------------------------------
    // 1. CREATE: Insert Item
    // ------------------------------------------------------------------
    public String insertToDatabase_Item() {
        // SQL column names must match your DB structure: ItemCode, ItemName, Category
        String query = "INSERT INTO Items (ItemCode, ItemName, Category) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.setString(3, category);

            pstmt.executeUpdate();
            return "✓ Item '" + name + "' successfully inserted.";

        } catch (SQLException e) {
            String errorMsg = "Error inserting item: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMsg, "Database Error", JOptionPane.ERROR_MESSAGE);
            return "❌ " + errorMsg;
        }
    }

    // ------------------------------------------------------------------
    // 2. READ: Get All Items (for JTable)
    // ------------------------------------------------------------------
    public static Vector<Vector<Object>> getAllItemsForTable() {
        Vector<Vector<Object>> data = new Vector<>();
        // SQL column names must match your DB structure
        String query = "SELECT ItemID, ItemCode, ItemName, Category FROM Items ORDER BY ItemID";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                // Column names must match the names returned by the query
                row.add(rs.getString("ItemID"));
                row.add(rs.getString("ItemCode"));
                row.add(rs.getString("ItemName"));
                row.add(rs.getString("Category"));
                data.add(row);
            }

        } catch (SQLException e) {
            String errorMsg = "Error loading item data: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMsg, "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return data;
    }

    // ------------------------------------------------------------------
    // 3. UPDATE: Update Item
    // ------------------------------------------------------------------
    public static String updateItemInDatabase(String itemID, String newCode, String newName, String newCategory) {
        // Note the ItemID is used in the WHERE clause
        String updateQuery = "UPDATE Items SET ItemCode = ?, ItemName = ?, Category = ? WHERE ItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {

            updateStmt.setString(1, newCode);
            updateStmt.setString(2, newName);
            updateStmt.setString(3, newCategory);
            updateStmt.setString(4, itemID);

            int rowsUpdated = updateStmt.executeUpdate();

            if (rowsUpdated > 0) {
                return "✓ Item ID " + itemID + " successfully updated.";
            } else {
                return "⚠️ No item updated. Item ID not found or no change in values.";
            }
        } catch (SQLException e) {
            String errorMsg = "Error updating item: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMsg, "Database Error", JOptionPane.ERROR_MESSAGE);
            return "❌ " + errorMsg;
        }
    }
    
    // ------------------------------------------------------------------
    // 4. DELETE: Delete Item
    // ------------------------------------------------------------------
    public static String deleteItemFromDatabase(String itemID) {
        String deleteQuery = "DELETE FROM Items WHERE ItemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            deleteStmt.setString(1, itemID);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                return "✓ Item ID " + itemID + " successfully deleted.";
            } else {
                return "⚠️ No item deleted. Item ID " + itemID + " not found.";
            }
        } catch (SQLException e) {
            String errorMsg = "Error deleting item: " + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMsg, "Database Error", JOptionPane.ERROR_MESSAGE);
            return "❌ " + errorMsg;
        }
    }
}