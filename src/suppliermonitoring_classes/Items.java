package suppliermonitoring_classes;

import java.util.Scanner;
import java.sql.*;

public class Items {
    private String itemID;  // Auto-generated
    private int code;
    private String name;
    private String category;

    public Items(String itemID, int code, String name, String category) {
        this.itemID = itemID;
        this.code = code;
        this.name = name;
        this.category = category;
    }

    // Input from user (no itemID, since it's auto_increment)
    public static Items inputItem() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- ADD ITEM ---");

        int code = getIntInput(sc, "Item Code: ");
        String name = getStringInput(sc, "Item Name: ");
        String category = getStringInput(sc, "Category: ");

        // itemID = null because MySQL will auto-generate it
        return new Items(null, code, name, category);
    }

    public void displayInfo() {
        System.out.println("Item ID: " + itemID);
        System.out.println("Code: " + code);
        System.out.println("Name: " + name);
        System.out.println("Category: " + category);
    }

    public void updateInfo(Scanner sc) {
        System.out.println("\n--- UPDATE ITEM ---");
        System.out.println("Leave blank to keep current value.");

        System.out.print("New Name [" + name + "]: ");
        String newName = sc.nextLine();
        if (!newName.isEmpty()) name = newName;

        System.out.print("New Category [" + category + "]: ");
        String newCategory = sc.nextLine();
        if (!newCategory.isEmpty()) category = newCategory;

        System.out.println("✓ Item updated successfully!");
    }

    private static int getIntInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    private static String getStringInput(Scanner sc, String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    public void insertToDatabase_Item() {
        // itemID is auto_increment, so we exclude it from INSERT
        String query = "INSERT INTO item (code, name, category) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, code);
            pstmt.setString(2, name);
            pstmt.setString(3, category);

            pstmt.executeUpdate();
            System.out.println("Item successfully inserted into database!");

        } catch (SQLException e) {
            System.err.println("Error inserting item: " + e.getMessage());
        }
    }
    
    public static void viewItems() {
    String query = "SELECT * FROM item";

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        System.out.println("\n--- ITEMS IN DATABASE ---");

        boolean hasResults = false;
        while (rs.next()) {
            hasResults = true;
            String itemID = rs.getString("itemID");
            int code = rs.getInt("code");
            String name = rs.getString("name");
            String category = rs.getString("category");

            System.out.println("\nItem ID: " + itemID);
            System.out.println("Code: " + code);
            System.out.println("Name: " + name);
            System.out.println("Category: " + category);
        }

        if (!hasResults) {
            System.out.println("No items found in the database.");
        }

    } catch (SQLException e) {
        System.err.println("Error retrieving items: " + e.getMessage());
        }
    }
    
      public static void updateItemInDatabase() {
        Scanner sc = new Scanner(System.in);

        // Show existing items
        viewItems();

        System.out.print("\nEnter the Item ID you want to update: ");
        String itemID = sc.nextLine();

        // Ask for new values
        System.out.print("Enter new name (leave blank to keep current): ");
        String newName = sc.nextLine();

        System.out.print("Enter new category (leave blank to keep current): ");
        String newCategory = sc.nextLine();

        // Fetch current values (to preserve blank fields)
        String currentName = null, currentCategory = null;

        String selectQuery = "SELECT name, category FROM item WHERE itemID = ?";
        String updateQuery = "UPDATE item SET name = ?, category = ? WHERE itemID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {

            selectStmt.setString(1, itemID);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                currentName = rs.getString("name");
                currentCategory = rs.getString("category");
            } else {
                System.out.println("❌ Item not found.");
                return;
            }

            // If blank, keep existing values
            if (newName.isEmpty()) newName = currentName;
            if (newCategory.isEmpty()) newCategory = currentCategory;

            // Now perform update
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setString(1, newName);
                updateStmt.setString(2, newCategory);
                updateStmt.setString(3, itemID);

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("✓ Item successfully updated in the database!");
                } else {
                    System.out.println("❌ No item updated. Please check the Item ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating item: " + e.getMessage());
        }
    }

}