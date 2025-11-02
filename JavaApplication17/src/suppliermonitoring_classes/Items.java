package suppliermonitoring_classes;

import java.sql.*;
import java.util.Vector;
<<<<<<< Updated upstream
import java.util.List;
import java.util.ArrayList;

public class Items {
    private final String itemID;
    private final int code;
    private final String name;
    private final String category;

    /**
     * Constructor used when creating a new item for insertion or fetching from DB.
     * @param itemID
     * @param code
     * @param name
     * @param category
=======

public class Items {
    private String itemID;
    private int code;
    private String name;
    private String category;

    /**
     * Constructor used when creating a new item for insertion or fetching from DB.
>>>>>>> Stashed changes
     */
    public Items(String itemID, int code, String name, String category) {
        this.itemID = itemID;
        this.code = code;
        this.name = name;
        this.category = category;
    }

    // --- Getters for GUI binding (optional but useful) ---
    public String getItemID() { return itemID; }
    public int getCode() { return code; }
    public String getName() { return name; }
    public String getCategory() { return category; }


    /**
     * Inserts the current Item object's data into the database.
     * @return A status message (success or error).
     */
    public String insertToDatabase_Item() {
        String query = "INSERT INTO item (code, name, category) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, code);
            pstmt.setString(2, name);
            pstmt.setString(3, category);

            pstmt.executeUpdate();
            return "✓ Item '" + name + "' successfully inserted.";

        } catch (SQLException e) {
            return "❌ Error inserting item: " + e.getMessage();
        }
    }

<<<<<<< Updated upstream
    public static Vector<Vector<Object>> getAllItemsForTable() {
        String query = "SELECT itemID, code, name, category FROM item";
        
        // Use modern List/ArrayList internally to avoid the obsolete collection warning
        List<List<Object>> listData = new ArrayList<>(); 
=======
    /**
     * Retrieves all items and formats the data for a JTable model.
     * @return Vector of Vectors containing all item data.
     */
    public static Vector<Vector<Object>> getAllItemsForTable() {
        String query = "SELECT itemID, code, name, category FROM item";
        Vector<Vector<Object>> tableData = new Vector<>();
>>>>>>> Stashed changes

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
<<<<<<< Updated upstream
                // Use ArrayList for the row data
                List<Object> row = new ArrayList<>();
=======
                Vector<Object> row = new Vector<>();
>>>>>>> Stashed changes
                row.add(rs.getString("itemID"));
                row.add(rs.getInt("code"));
                row.add(rs.getString("name"));
                row.add(rs.getString("category"));
<<<<<<< Updated upstream
                listData.add(row);
=======
                tableData.add(row);
>>>>>>> Stashed changes
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving items for table: " + e.getMessage());
        }
<<<<<<< Updated upstream
        
     
        Vector<Vector<Object>> tableData = new Vector<>();
        for (List<Object> listRow : listData) {
            // Convert each internal List row to a Vector row
            tableData.add(new Vector<>(listRow)); 
        }

=======
>>>>>>> Stashed changes
        return tableData;
    }

    /**
     * Updates an existing item record in the database.
     * @param itemID The ID of the item to update.
     * @param newName The new name (or empty string to keep current).
     * @param newCategory The new category (or empty string to keep current).
     * @return A status message.
     */
    public static String updateItemInDatabase(String itemID, String newName, String newCategory) {
        String selectQuery = "SELECT name, category FROM item WHERE itemID = ?";
        String updateQuery = "UPDATE item SET name = ?, category = ? WHERE itemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, itemID);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                return "❌ Item ID " + itemID + " not found.";
            }
            
<<<<<<< Updated upstream

            String currentName = rs.getString("name");
            String currentCategory = rs.getString("category");
            
    
            String finalName = newName.isEmpty() ? currentName : newName;
            String finalCategory = newCategory.isEmpty() ? currentCategory : newCategory;
            
   
=======
            // Get current values
            String currentName = rs.getString("name");
            String currentCategory = rs.getString("category");
            
            // Determine final values
            String finalName = newName.isEmpty() ? currentName : newName;
            String finalCategory = newCategory.isEmpty() ? currentCategory : newCategory;
            
            // Perform update
>>>>>>> Stashed changes
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, finalName);
                updateStmt.setString(2, finalCategory);
                updateStmt.setString(3, itemID);

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    return "✓ Item ID " + itemID + " successfully updated.";
                } else {
                    return "⚠️ No item updated. Check if values were changed.";
                }
            }

        } catch (SQLException e) {
            return "❌ Error updating item: " + e.getMessage();
        }
    }

    /**
     * Deletes an item record from the database.
     * @param itemID The ID of the item to delete.
     * @return A status message.
     */
    public static String deleteItemFromDatabase(String itemID) {
        String deleteQuery = "DELETE FROM item WHERE itemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {

            deleteStmt.setString(1, itemID);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                return "✓ Item ID " + itemID + " successfully deleted.";
            } else {
                return "❌ Item ID " + itemID + " not found.";
            }

        } catch (SQLException e) {
            return "❌ Error deleting item: " + e.getMessage();
        }
    }
}
