/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package suppliermonitoring_classes;

import java.sql.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 *
 * @author Elaine
 */
public class Deliver extends javax.swing.JFrame {

     private void loadData() {
    //Connection con = DatabaseConnection.getConnection();
    String status = null;
    Connection con = (Connection) DatabaseConnection.getConnection();

    if (con == null) {
        status = "xxxxx";
        JOptionPane.showMessageDialog(this, "Database connection failed!");
        return;
    } else {
        status = "OK";
    }
    try {
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM delivers");      
        DefaultTableModel model = (DefaultTableModel) tblDelivers.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            Object[] row = {
                rs.getString("delivery_id"),
                rs.getString("supplierID"),
                rs.getString("itemID"),
                rs.getString("consignor"),
                rs.getString("supplier_name"),
                rs.getString("item_name"),
                rs.getInt("quantity"),
                rs.getDouble("price"),
                rs.getString("date")
            };
            model.addRow(row);
        }

        con.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading data: " + e.getMessage());
    }
}
    private void loadComboBoxes() {
    try (Connection con = DatabaseConnection.getConnection()) {
        supCmb.removeAllItems();
        supNameCmb.removeAllItems();
        itmCmb.removeAllItems();
        itmNameCmb.removeAllItems();
        

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT supplierID, supplier_name FROM suppliers");
        while (rs.next()) {
            supCmb.addItem(rs.getString("supplierID"));
            supNameCmb.addItem(rs.getString("supplier_name"));
        }
        rs = st.executeQuery("SELECT itemID, item_name FROM items");
        while (rs.next()) {
            itmCmb.addItem(rs.getString("itemID"));
            itmNameCmb.addItem(rs.getString("item_name"));
        }
        rs.close();
        st.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading combo boxes: " + e.getMessage());
    }
    }
    private void updateData(){
        int selectedRow = tblDelivers.getSelectedRow();
        
        if (selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please Select row to update");
            return;
        }
    String deliveryID = deltxt.getText();
    String supplierID = supCmb.getSelectedItem().toString();
    String itemID = itmCmb.getSelectedItem().toString();
    String consignor = consignorTxt.getText();
    String supplierName = supNameCmb.getSelectedItem().toString();
    String itemName = itmNameCmb.getSelectedItem().toString();
    java.util.Date selectedDate = dt.getDate();
    int quantity = 0;
    double price = 0.0;
    
    try {
        quantity = Integer.parseInt(qnty.getText());
        price = Double.parseDouble(prc.getText());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numbers.");
        return;
    }
    if (deliveryID.isEmpty() || consignor.isEmpty() || selectedDate == null) {
        JOptionPane.showMessageDialog(this, "All fields are required!");
        return;
    }
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to update Delivery ID " + deliveryID + "?","Confirm Update", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }
    String sql = "UPDATE delivers SET supplierID = ?, itemID = ?, consignor = ?, supplier_name = ?, item_name = ?, quantity = ?, price = ?, date = ? WHERE delivery_id = ?";
    try (
        Connection con = DatabaseConnection.getConnection(); 
        PreparedStatement pstmt = con.prepareStatement(sql)) 
    {
        pstmt.setString(1, deliveryID);
        pstmt.setString(2, supplierID);
        pstmt.setString(3, itemID);
        pstmt.setString(4, consignor);
        pstmt.setString(5, supplierName);
        pstmt.setString(6, itemName);
        pstmt.setInt(7, quantity);
        pstmt.setDouble(8, price);
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        pstmt.setDate(8, sqlDate);

        int rowsUpdated = pstmt.executeUpdate();

        if (rowsUpdated > 0) {
            JOptionPane.showMessageDialog(this, "Record updated successfully!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "No record found with that Delivery ID.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
    }
    private void addData(){
    String deliveryID = deltxt.getText();
    String supplierID = supCmb.getSelectedItem().toString();
    String itemID = itmCmb.getSelectedItem().toString();
    String consignor = consignorTxt.getText();
    String supplierName = supNameCmb.getSelectedItem().toString();
    String itemName = itmNameCmb.getSelectedItem().toString();
    java.util.Date selectedDate = dt.getDate();
    int quantity = 0;
    double price = 0.0;
    
    try {
            quantity = Integer.parseInt(qnty.getText());
            price = Double.parseDouble(prc.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Quantity snd Price Must be valid");
        }
    if (deliveryID.isEmpty() || consignor.isEmpty() || selectedDate == null) {
        JOptionPane.showMessageDialog(this, "All fields are required!");
        return;
    }
    String sql = "insert into delivers (delivery_id, supplierID, itemID, consignor, supplier_name, item_name, quantity, price, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    try (
        Connection con = DatabaseConnection.getConnection(); 
        PreparedStatement pstmt = con.prepareStatement(sql)
    ) {
        pstmt.setString(1, deliveryID);
        pstmt.setString(2, supplierID);
        pstmt.setString(3, itemID);
        pstmt.setString(4, consignor);
        pstmt.setString(5, supplierName);
        pstmt.setString(6, itemName);
        pstmt.setInt(7, quantity);
        pstmt.setDouble(8, price);
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        pstmt.setDate(9, sqlDate);
        pstmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Record added successfully!");

        deltxt.setText("");
        consignorTxt.setText("");
        qnty.setText("");
        prc.setText("");
        dt.setDate(null);
        supCmb.setSelectedIndex(0);
        itmCmb.setSelectedIndex(0);
        supNameCmb.setSelectedIndex(0);
        itmNameCmb.setSelectedIndex(0);
        loadData();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
    }
    private void deleteData() {
        int selectedRow = tblDelivers.getSelectedRow();
        
        if (selectedRow == -1){
            javax.swing.JOptionPane.showMessageDialog(this, "Please select a record to delete.");
            return;
        }
    String deliveryID = deltxt.getText();
    if (deliveryID.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter or select a Delivery ID to delete.");
        return;
    }
    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) {
        return; 
    }
    String sql = "DELETE FROM delivers WHERE delivery_id = ?";

    try (Connection con = DatabaseConnection.getConnection();  
        PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setString(1, deliveryID);

        int rowsAffected = pstmt.executeUpdate();

        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");
            deltxt.setText("");
            consignorTxt.setText("");
            qnty.setText("");
            prc.setText("");
            dt.setDate(null);
            supCmb.setSelectedIndex(0);
            itmCmb.setSelectedIndex(0);
            supNameCmb.setSelectedIndex(0);
            itmNameCmb.setSelectedIndex(0);      
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "No record found with that Delivery ID.");
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
    }
    private void exportData(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save CSV FIle");
        
        String timestamp = new SimpleDateFormat("yyyy-MM-DD_HHmmss").format(new java.util.Date());
        chooser.setSelectedFile(new java.io.File("export_" + timestamp + ".csv"));
        
        int userSelection = chooser.showSaveDialog(this);
        
        if(userSelection == JFileChooser.APPROVE_OPTION){
            File fileToSave = chooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            
            String query = "SELECT * FROM delivers";
            try (
                Connection con = DatabaseConnection.getConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                PrintWriter pw = new PrintWriter(new FileWriter(filePath)))
            {
                ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                pw.print(meta.getColumnName(i));
                if (i < columnCount) pw.print(",");
            }
            pw.println();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    pw.print(rs.getString(i));
                    if (i < columnCount) pw.print(",");
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Data exported successfully to:\n" + filePath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage());
        }
    }
}
    public Deliver() {
        initComponents();
        loadData();
        loadComboBoxes();
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
        deltxt = new javax.swing.JTextField();
        supCmb = new javax.swing.JComboBox<>();
        itmCmb = new javax.swing.JComboBox<>();
        consignorTxt = new javax.swing.JTextField();
        qnty = new javax.swing.JTextField();
        prc = new javax.swing.JTextField();
        supNameCmb = new javax.swing.JComboBox<>();
        itmNameCmb = new javax.swing.JComboBox<>();
        dt = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        svbtn = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDelivers = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        upbtn = new javax.swing.JButton();
        expbtn = new javax.swing.JButton();
        dltbtn = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        itmbtn = new javax.swing.JButton();
        supbtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1196, 627));

        jPanel1.setPreferredSize(new java.awt.Dimension(420, 500));

        supCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        itmCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        supNameCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        itmNameCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Create New");

        svbtn.setText("SAVE");
        svbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                svbtnActionPerformed(evt);
            }
        });

        jLabel5.setText("Delivery ID");

        jLabel6.setText("Supplier ID");

        jLabel7.setText("Item ID");

        jLabel8.setText("Consignor");

        jLabel9.setText("Supplier Name");

        jLabel10.setText("Item Name");

        jLabel11.setText("Quantity");

        jLabel12.setText("Price");

        jLabel13.setText("Date");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(deltxt)
                                .addComponent(supCmb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(itmCmb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(consignorTxt)
                                .addComponent(supNameCmb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(itmNameCmb, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(qnty)
                                .addComponent(prc)
                                .addComponent(dt, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(svbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(5, 5, 5)
                .addComponent(deltxt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(2, 2, 2)
                .addComponent(supCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(consignorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(supNameCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itmNameCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qnty, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prc, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dt, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(svbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(420, 500));

        jLabel2.setText("Deliver Table");

        tblDelivers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Deliver ID", "Supplier ID", "Item ID", "Consignor", "Supplier Name", "Item Name", "Quantity", "Price", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblDelivers);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("Action");

        upbtn.setText("UPDATE");
        upbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upbtnActionPerformed(evt);
            }
        });

        expbtn.setText("EXPORT");
        expbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expbtnActionPerformed(evt);
            }
        });

        dltbtn.setText("DELETE");
        dltbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dltbtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(upbtn, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                            .addComponent(expbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dltbtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(expbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dltbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel4.setText("Deliver Record");

        itmbtn.setText("item Button");

        supbtn.setText("Supplier button");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(itmbtn)
                        .addGap(18, 18, 18)
                        .addComponent(supbtn))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(21, 21, 21))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(supbtn)
                        .addComponent(itmbtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(56, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void svbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_svbtnActionPerformed
        addData();
    }//GEN-LAST:event_svbtnActionPerformed

    private void upbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upbtnActionPerformed
        updateData();
    }//GEN-LAST:event_upbtnActionPerformed

    private void expbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expbtnActionPerformed
        exportData();
    }//GEN-LAST:event_expbtnActionPerformed

    private void dltbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dltbtnActionPerformed
        deleteData();
    }//GEN-LAST:event_dltbtnActionPerformed

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
            java.util.logging.Logger.getLogger(Deliver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Deliver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Deliver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Deliver.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Deliver().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField consignorTxt;
    private javax.swing.JTextField deltxt;
    private javax.swing.JButton dltbtn;
    private com.toedter.calendar.JDateChooser dt;
    private javax.swing.JButton expbtn;
    private javax.swing.JComboBox<String> itmCmb;
    private javax.swing.JComboBox<String> itmNameCmb;
    private javax.swing.JButton itmbtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField prc;
    private javax.swing.JTextField qnty;
    private javax.swing.JComboBox<String> supCmb;
    private javax.swing.JComboBox<String> supNameCmb;
    private javax.swing.JButton supbtn;
    private javax.swing.JButton svbtn;
    private javax.swing.JTable tblDelivers;
    private javax.swing.JButton upbtn;
    // End of variables declaration//GEN-END:variables
}
