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
    Connection con = DatabaseConnection.getConnection();
    String status = null;

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
        DefaultTableModel model = (DefaultTableModel) dltbl.getModel();
        model.setRowCount(0);

        while (rs.next()) {
            Object[] row = {
                rs.getString("deliveryID"),
                rs.getString("supplierID"),
                rs.getString("itemID"),
                rs.getString("consignor"),
                rs.getString("full_name"),
                rs.getString("name"),
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
        ResultSet rs = st.executeQuery("SELECT supplierID, full_name FROM supplier");
        while (rs.next()) {
            supCmb.addItem(rs.getString("supplierID"));
            supNameCmb.addItem(rs.getString("full_name"));
        }
        rs = st.executeQuery("SELECT itemID, name FROM item");
        while (rs.next()) {
            itmCmb.addItem(rs.getString("itemID"));
            itmNameCmb.addItem(rs.getString("name"));
        }
        rs.close();
        st.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading combo boxes: " + e.getMessage());
    }
    }
    private void updateData(){
    int selectedRow = dltbl.getSelectedRow();
    if (selectedRow == -1){
        JOptionPane.showMessageDialog(this, "Please select a row to update.");
        return;
    }

    String deliveryID = dltbl.getValueAt(selectedRow, 0).toString(); // get ID from table
    String supplierID = supCmb.getSelectedItem().toString();
    String itemID = itmCmb.getSelectedItem().toString();
    String consignor = contxt.getText();
    String supplierName = supNameCmb.getSelectedItem().toString();
    String itemName = itmNameCmb.getSelectedItem().toString();
    java.util.Date selectedDate = dt.getDate();

    int quantity;
    double price;
    try {
        quantity = Integer.parseInt(qnty.getText());
        price = Double.parseDouble(prc.getText());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numbers.");
        return;
    }

    if (consignor.isEmpty() || selectedDate == null) {
        JOptionPane.showMessageDialog(this, "All fields are required!");
        return;
    }

    String sql = "UPDATE delivers SET supplierID=?, itemID=?, consignor=?, full_name=?, name=?, quantity=?, price=?, date=? WHERE deliveryID=?";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setString(1, supplierID);
        pstmt.setString(2, itemID);
        pstmt.setString(3, consignor);
        pstmt.setString(4, supplierName);
        pstmt.setString(5, itemName);
        pstmt.setInt(6, quantity);
        pstmt.setDouble(7, price);
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        pstmt.setDate(8, sqlDate);
        pstmt.setString(9, deliveryID);

        int rows = pstmt.executeUpdate();
        if (rows > 0) {
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
    String supplierID = supCmb.getSelectedItem().toString();
    String itemID = itmCmb.getSelectedItem().toString();
    String consignor = contxt.getText();
    String supplierName = supNameCmb.getSelectedItem().toString();
    String itemName = itmNameCmb.getSelectedItem().toString();
    java.util.Date selectedDate = dt.getDate();
    int quantity;
    double price;

    try {
        quantity = Integer.parseInt(qnty.getText());
        price = Double.parseDouble(prc.getText());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numbers.");
        return;
    }

    if (consignor.isEmpty() || selectedDate == null) {
        JOptionPane.showMessageDialog(this, "All fields are required!");
        return;
    }

    String sql = "INSERT INTO delivers (supplierID, itemID, consignor, full_name, name, quantity, price, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {

        pstmt.setString(1, supplierID);
        pstmt.setString(2, itemID);
        pstmt.setString(3, consignor);
        pstmt.setString(4, supplierName);
        pstmt.setString(5, itemName);
        pstmt.setInt(6, quantity);
        pstmt.setDouble(7, price);
        java.sql.Date sqlDate = new java.sql.Date(selectedDate.getTime());
        pstmt.setDate(8, sqlDate);

        pstmt.executeUpdate();

        JOptionPane.showMessageDialog(this, "Record added successfully!");
        contxt.setText("");
        qnty.setText("");
        prc.setText("");
        dt.setDate(null);
        loadData();

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
    }
}
    private void deleteData() {
    int selectedRow = dltbl.getSelectedRow();
    if (selectedRow == -1){
        JOptionPane.showMessageDialog(this, "Please select a record to delete.");
        return;
    }

    String deliveryID = dltbl.getValueAt(selectedRow, 0).toString();

    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete Delivery ID " + deliveryID + "?",
        "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    String sql = "DELETE FROM delivers WHERE deliveryID=?";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement pstmt = con.prepareStatement(sql)) {
        pstmt.setString(1, deliveryID);
        int rows = pstmt.executeUpdate();

        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Record deleted successfully!");
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Record not found!");
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
        supCmb = new javax.swing.JComboBox<>();
        itmCmb = new javax.swing.JComboBox<>();
        contxt = new javax.swing.JTextField();
        supNameCmb = new javax.swing.JComboBox<>();
        itmNameCmb = new javax.swing.JComboBox<>();
        qnty = new javax.swing.JTextField();
        prc = new javax.swing.JTextField();
        svbtn = new javax.swing.JButton();
        dt = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        dltbl = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        updbtn = new javax.swing.JButton();
        expbtn = new javax.swing.JButton();
        delbtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        itmbtn = new javax.swing.JButton();
        supbtn = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1196, 627));

        jPanel1.setPreferredSize(new java.awt.Dimension(420, 500));

        supCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        itmCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        supNameCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        itmNameCmb.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        itmNameCmb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itmNameCmbActionPerformed(evt);
            }
        });

        svbtn.setText("SAVE");
        svbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                svbtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Create New");

        jLabel3.setText("Supplier ID");

        jLabel4.setText("Item ID");

        jLabel5.setText("Consignor");

        jLabel6.setText("Supplier Name");

        jLabel7.setText("Item Name");

        jLabel8.setText("Quantity");

        jLabel9.setText("Price");

        jLabel10.setText("Date");

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
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(prc, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(qnty, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(itmNameCmb, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(supNameCmb, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(contxt, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(itmCmb, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(supCmb, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(dt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(svbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(supCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itmCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contxt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(supNameCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(itmNameCmb, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qnty, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(prc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dt, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(svbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(420, 500));

        dltbl.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(dltbl);

        jLabel11.setText("Deliver Table");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel11)
                .addGap(11, 11, 11)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 552, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        updbtn.setText("UPDATE");
        updbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updbtnActionPerformed(evt);
            }
        });

        expbtn.setText("EXPORT");
        expbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expbtnActionPerformed(evt);
            }
        });

        delbtn.setText("DELETE");
        delbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delbtnActionPerformed(evt);
            }
        });

        jLabel12.setText("Action");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(29, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(delbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addGap(4, 4, 4)
                .addComponent(updbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(expbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(delbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        itmbtn.setText("Item Record");
        itmbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itmbtnActionPerformed(evt);
            }
        });

        supbtn.setText("Supplier Record");
        supbtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supbtnActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 36)); // NOI18N
        jLabel13.setText("Deliver Records");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(itmbtn)
                        .addGap(18, 18, 18)
                        .addComponent(supbtn)))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(itmbtn)
                        .addComponent(supbtn))
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void itmNameCmbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmNameCmbActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itmNameCmbActionPerformed

    private void itmbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itmbtnActionPerformed
        Item tm = new Item();
        tm.setVisible(true);
        dispose();
    }//GEN-LAST:event_itmbtnActionPerformed

    private void svbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_svbtnActionPerformed
        addData();
    }//GEN-LAST:event_svbtnActionPerformed

    private void updbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updbtnActionPerformed
       updateData();
    }//GEN-LAST:event_updbtnActionPerformed

    private void expbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expbtnActionPerformed
       exportData();
    }//GEN-LAST:event_expbtnActionPerformed

    private void delbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delbtnActionPerformed
        deleteData();
    }//GEN-LAST:event_delbtnActionPerformed

    private void supbtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supbtnActionPerformed
        Supplier sp = new Supplier();
        sp.setVisible(true);
        dispose();
    }//GEN-LAST:event_supbtnActionPerformed

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
    private javax.swing.JTextField contxt;
    private javax.swing.JButton delbtn;
    private javax.swing.JTable dltbl;
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
    private javax.swing.JButton updbtn;
    // End of variables declaration//GEN-END:variables
}
