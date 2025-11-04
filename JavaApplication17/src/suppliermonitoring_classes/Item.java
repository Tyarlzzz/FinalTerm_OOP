/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package suppliermonitoring_classes;

import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat; 
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import suppliermonitoring_classes.DatabaseConnection; 
/**
 *
 * @author Elaine
 */
public class Item extends javax.swing.JFrame {

    /**
     * Creates new form Item
     */
    public Item() {
        initComponents();
        loadItems(); 
       
        itemTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemTableMouseClicked(evt); 
            }
        });
        }
    
    private void loadItems() {
        DefaultTableModel model = (DefaultTableModel) itemTable.getModel();
        model.setRowCount(0);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT itemID, code, name, category FROM Item")) {


            model.setColumnIdentifiers(new String[]{"ID", "Code", "Name", "Category"});

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("itemID"),
                    rs.getString("code"),
                    rs.getString("name"),
                    rs.getString("category")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading items: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
private void saveItem() {
        String code = itemCodetxt.getText().trim();
        String name = itemNametxt.getText().trim();
        String category = itemCategorytxt.getText().trim();

        if (code.isEmpty() || name.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please input in all required fields (Code, Name, Category)!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO Item (code, name, category) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, category);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Operation was successful.
                loadItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Item added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Item insertion failed (0 rows affected).", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 private void updateItem() {
        int row = itemTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to update!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object itemIdValue = itemTable.getValueAt(row, 0);
        if (itemIdValue == null) {
            JOptionPane.showMessageDialog(this, "Could not retrieve Item ID from the table.", "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int itemID;
        try {
            itemID = Integer.parseInt(itemIdValue.toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Item ID format.", "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String code = itemCodetxt.getText().trim();
        String name = itemNametxt.getText().trim();
        String category = itemCategorytxt.getText().trim();

        if (code.isEmpty() || name.isEmpty() || category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required for update!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE Item SET code=?, name=?, category=? WHERE itemID=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, category);
            ps.setInt(4, itemID);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                // Operation was successful.
                loadItems();
                clearFields();
                JOptionPane.showMessageDialog(this, "Item updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed: Item with ID " + itemID + " not found or no changes were made.", "Update Failed", JOptionPane.WARNING_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
private void deleteItem() {
        int row = itemTable.getSelectedRow();

        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete!", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int itemID;
        try {
            itemID = Integer.parseInt(itemTable.getValueAt(row, 0).toString());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Item ID format from table.", "Data Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM Item WHERE itemID=?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, itemID);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    // Operation was successful.
                    loadItems();
                    clearFields();
                    JOptionPane.showMessageDialog(this, "Item deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Deletion failed: Item with ID " + itemID + " not found.", "Deletion Failed", JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting item: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
private void exportItem() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Save Items CSV File");
    
    String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new java.util.Date());
    chooser.setSelectedFile(new java.io.File("items_" + timestamp + ".csv"));
    
    int userSelection = chooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = chooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        String query = "SELECT itemID, code, name, category FROM Item";

        try (
            Connection con = DatabaseConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            PrintWriter pw = new PrintWriter(new FileWriter(filePath)) 
        ) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                pw.print(meta.getColumnLabel(i)); 
                if (i < columnCount) pw.print(",");
            }
            pw.println();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    if (value != null && value.contains(",")) {
                        pw.print("\"" + value.replace("\"", "\"\"") + "\"");
                    } else {
                        pw.print(value != null ? value : "");
                    }
                    
                    if (i < columnCount) pw.print(",");
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Items exported successfully to:\n" + filePath);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage());
        }
    }
}
private void clearFields() { 
    itemCodetxt.setText("");
    itemNametxt.setText("");
    itemCategorytxt.setText(""); 
}

private void populateFieldsFromTable() {
    int selectedRow = itemTable.getSelectedRow();
    
    if (selectedRow >= 0) {
        // Data in the table columns: 0=itemID, 1=code, 2=name, 3=category
        itemCodetxt.setText(itemTable.getValueAt(selectedRow, 1).toString());
        itemNametxt.setText(itemTable.getValueAt(selectedRow, 2).toString());
        itemCategorytxt.setText(itemTable.getValueAt(selectedRow, 3).toString());
    }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        itemCodetxt = new javax.swing.JTextField();
        itemNametxt = new javax.swing.JTextField();
        itemCategorytxt = new javax.swing.JTextField();
        saveItem = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        itemTable = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        updateItem = new javax.swing.JButton();
        exportItem = new javax.swing.JButton();
        deleteItem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Create New");

        itemCodetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCodetxtActionPerformed(evt);
            }
        });

        itemNametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemNametxtActionPerformed(evt);
            }
        });

        itemCategorytxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCategorytxtActionPerformed(evt);
            }
        });

        saveItem.setText("SAVE");
        saveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemActionPerformed(evt);
            }
        });

        jLabel3.setText("Item Code");

        jLabel4.setText("Item Name");

        jLabel5.setText("Category");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(saveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(itemCodetxt)
                                .addComponent(itemNametxt)
                                .addComponent(itemCategorytxt, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(325, 325, 325)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(2, 2, 2)
                .addComponent(itemCodetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itemNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(3, 3, 3)
                .addComponent(itemCategorytxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(saveItem, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(176, Short.MAX_VALUE))
        );

        itemTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Item ID", "Item Code", "Item Name", "Category"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(itemTable);
        if (itemTable.getColumnModel().getColumnCount() > 0) {
            itemTable.getColumnModel().getColumn(0).setHeaderValue("Item ID");
        }

        jLabel6.setText("Item List");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel7.setText("Actions");

        updateItem.setText("UPDATE");
        updateItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateItemActionPerformed(evt);
            }
        });

        exportItem.setText("EXPORT");
        exportItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportItemActionPerformed(evt);
            }
        });

        deleteItem.setText("DELETE");
        deleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(79, 129, Short.MAX_VALUE))
                    .addComponent(updateItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(exportItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteItem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(updateItem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(exportItem, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deleteItem, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exportItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportItemActionPerformed
       exportItem(); // TODO add your handling code here:
    }//GEN-LAST:event_exportItemActionPerformed

    private void deleteItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemActionPerformed
        deleteItem();// TODO add your handling code here:
    }//GEN-LAST:event_deleteItemActionPerformed

    private void updateItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateItemActionPerformed
       updateItem(); // TODO add your handling code here:
    }//GEN-LAST:event_updateItemActionPerformed

    private void itemCodetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCodetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemCodetxtActionPerformed

    private void itemNametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemNametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemNametxtActionPerformed

    private void itemCategorytxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCategorytxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemCategorytxtActionPerformed

    private void saveItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemActionPerformed
       saveItem(); // TODO add your handling code here:
    }//GEN-LAST:event_saveItemActionPerformed
private void itemTableMouseClicked(java.awt.event.MouseEvent evt) {                                            
    populateFieldsFromTable();
}
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Item.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Item.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Item.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Item.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Item().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteItem;
    private javax.swing.JButton exportItem;
    private javax.swing.JTextField itemCategorytxt;
    private javax.swing.JTextField itemCodetxt;
    private javax.swing.JTextField itemNametxt;
    private javax.swing.JTable itemTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton saveItem;
    private javax.swing.JButton updateItem;
    // End of variables declaration//GEN-END:variables
}
