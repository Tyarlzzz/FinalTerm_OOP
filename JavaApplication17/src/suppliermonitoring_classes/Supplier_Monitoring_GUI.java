/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package suppliermonitoring_classes;

import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.util.Vector;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Supplier_Monitoring_GUI extends javax.swing.JFrame {

    private Connection conn;
    private final String ITEMS_TABLE = "Items";
    private final String SUPPLIERS_TABLE = "Suppliers";
    private final String DELIVERIES_TABLE = "Deliveries";

    public Supplier_Monitoring_GUI() {
        initComponents();
        connectDB();
        loadAllTableData();
        initListeners();
        // Set the default tab to the Item Record panel (index 0)
        jTabbedPane1.setSelectedIndex(0);
    }
private void connectDB() {
    try {
        // Call the centralized method from the DatabaseConnection class
        conn = DatabaseConnection.getConnection(); 
        
        if (conn == null) {
             JOptionPane.showMessageDialog(this, "Failed to establish database connection.\nPlease check DatabaseConnection.java or if your MySQL server is running.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        initializeDB(); 
        System.out.println("Database connected and initialized.");

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                DatabaseConnection.closeConnection(); // Use the DAO close method
            }
        });
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Database Error during startup: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    private void initializeDB() throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Items Table
            String createItems = "CREATE TABLE IF NOT EXISTS " + ITEMS_TABLE + " ("
                    + "ItemID TEXT PRIMARY KEY,"
                    + "ItemCode TEXT,"
                    + "ItemName TEXT,"
                    + "Category TEXT);";
            stmt.execute(createItems);

            // Suppliers Table
            String createSuppliers = "CREATE TABLE IF NOT EXISTS " + SUPPLIERS_TABLE + " ("
                    + "SupplierID TEXT PRIMARY KEY,"
                    + "SFullName TEXT NOT NULL,"
                    + "Address TEXT,"
                    + "PhoneTxt TEXT);";
            stmt.execute(createSuppliers);

            // Deliveries Table (Uses foreign keys)
            String createDeliveries = "CREATE TABLE IF NOT EXISTS " + DELIVERIES_TABLE + " ("
                    + "DeliverID TEXT PRIMARY KEY,"
                    + "SupplierID TEXT NOT NULL,"
                    + "ItemID TEXT NOT NULL,"
                    + "Consignor TEXT,"
                    + "Quantity INTEGER,"
                    + "Price REAL,"
                    + "Date TEXT,"
                    + "FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE,"
                    + "FOREIGN KEY(ItemID) REFERENCES Items(ItemID) ON DELETE CASCADE);";
            stmt.execute(createDeliveries);
        }
    }

    private void closeDBConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    private void loadItemsTable() {
   
    Vector<Vector<Object>> data = Items.getAllItemsForTable();
    
   
    Vector<String> columnNames = new Vector<>(java.util.Arrays.asList("Item ID", "Item Code", "Item Name", "Category"));
    
    jTable1.setModel(new DefaultTableModel(data, columnNames));
}
private void loadAllTableData() {
    loadItemsTable();
    // loadSuppliersTable(); // Implement these once the Supplier DAO is ready
    // loadDeliveriesTable(); // Implement these once the Delivery DAO is ready
}

    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnLabel(column));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void exportTableToCSV(JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File(defaultFileName));
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getAbsolutePath().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(fileToSave)) {
                TableModel model = table.getModel();

                // Write column headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    fw.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");

                // Write row data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object cellValue = model.getValueAt(i, j);
                        // Replace commas to avoid breaking CSV format
                        String value = (cellValue == null) ? "" : cellValue.toString().replace(",", ";");
                        fw.write(value + (j == model.getColumnCount() - 1 ? "" : ","));
                    }
                    fw.write("\n");
                }

                JOptionPane.showMessageDialog(this, "Data successfully exported to:\n" + fileToSave.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // --- Clear Field Helpers (User's original code) ---

    private void clearItemFields() {
        ItemIDtxt.setText("");
        ItemNameTxt.setText("");
        ItemcategoryTxt.setText("");
        itemCodeTxt.setText("");
    }

    private void clearSupplierFields() {
        SupplierIDtxt.setText("");
        SFullNametxt.setText("");
        Addresstxt.setText("");
        Phonetxt.setText("");
    }

    private void clearDeliveryFields() {
        DeliverIDtxt.setText("");
        Consignortxt.setText("");
        Pricnetxt.setText("");
        quantitytxt.setText("");
        Datetxt.setText("");
        jTextField2.setText(""); // Supplier ID
        jTextField3.setText(""); // Item ID
    }

    // --- Listener Initialization (User's original code) ---

    private void addRowSelectionListenerForItems() {
        jTable1.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && jTable1.getSelectedRow() != -1) {
                    int selectedRow = jTable1.getSelectedRow();
                    TableModel model = jTable1.getModel();

                    try {
                        // Columns: 0: ItemID, 1: ItemCode, 2: ItemName, 3: Category
                        ItemIDtxt.setText(model.getValueAt(selectedRow, 0).toString());
                        itemCodeTxt.setText(model.getValueAt(selectedRow, 1).toString());
                        ItemNameTxt.setText(model.getValueAt(selectedRow, 2).toString());
                        ItemcategoryTxt.setText(model.getValueAt(selectedRow, 3).toString());
                    } catch (Exception e) {
                        System.err.println("Error reading row data for Items: " + e.getMessage());
                        clearItemFields();
                    }
                }
            }
        });
    }

    private void addRowSelectionListenerForSuppliers() {
        jTable3.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && jTable3.getSelectedRow() != -1) {
                    int selectedRow = jTable3.getSelectedRow();
                    TableModel model = jTable3.getModel();

                    try {
                        // Columns: 0: SupplierID, 1: SFullName, 2: Address, 3: PhoneTxt
                        SupplierIDtxt.setText(model.getValueAt(selectedRow, 0).toString());
                        SFullNametxt.setText(model.getValueAt(selectedRow, 1).toString());
                        Addresstxt.setText(model.getValueAt(selectedRow, 2).toString());
                        Phonetxt.setText(model.getValueAt(selectedRow, 3).toString());
                    } catch (Exception e) {
                        System.err.println("Error reading row data for Suppliers: " + e.getMessage());
                        clearSupplierFields();
                    }
                }
            }
        });
    }

    private void addRowSelectionListenerForDeliveries() {
        jTable2.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting() && jTable2.getSelectedRow() != -1) {
                    int selectedRow = jTable2.getSelectedRow();
                    TableModel model = jTable2.getModel();

                    try {
                        // Deliveries joined query columns:
                        // 0: DeliverID, 1: SupplierID, 2: ItemID, 3: Consignor, 6: Quantity, 7: Price, 8: Date
                        DeliverIDtxt.setText(model.getValueAt(selectedRow, 0).toString());
                        jTextField2.setText(model.getValueAt(selectedRow, 1).toString()); // SupplierID
                        jTextField3.setText(model.getValueAt(selectedRow, 2).toString()); // ItemID
                        Consignortxt.setText(model.getValueAt(selectedRow, 3).toString());
                        quantitytxt.setText(model.getValueAt(selectedRow, 6).toString());
                        Pricnetxt.setText(model.getValueAt(selectedRow, 7).toString());
                        Datetxt.setText(model.getValueAt(selectedRow, 8).toString());
                    } catch (Exception e) {
                        System.err.println("Error reading row data for Deliveries: " + e.getMessage());
                        clearDeliveryFields();
                    }
                }
            }
        });
    }

    private void initListeners() {
        addRowSelectionListenerForItems();
        addRowSelectionListenerForSuppliers();
        addRowSelectionListenerForDeliveries();
    }



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel1 = new javax.swing.JPanel();
        ItemBtn = new javax.swing.JButton();
        DeliverBtn = new javax.swing.JButton();
        SupplierBtn = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        ItemPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        ItemIDtxt = new javax.swing.JTextField();
        itemCodeTxt = new javax.swing.JTextField();
        ItemNameTxt = new javax.swing.JTextField();
        ItemcategoryTxt = new javax.swing.JTextField();
        SaveItemBtn = new javax.swing.JButton();
        updItemBtn = new javax.swing.JButton();
        DeleteItemBtn = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        expitembtn = new javax.swing.JButton();
        SupplierPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        SupplierIDtxt = new javax.swing.JTextField();
        Addresstxt = new javax.swing.JTextField();
        SFullNametxt = new javax.swing.JTextField();
        Phonetxt = new javax.swing.JTextField();
        SaveSupBtn = new javax.swing.JButton();
        updSupplierBtn = new javax.swing.JButton();
        deleteSupBtn = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        expSupbtn = new javax.swing.JButton();
        DeliverPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        DeliverIDtxt = new javax.swing.JTextField();
        Consignortxt = new javax.swing.JTextField();
        Pricnetxt = new javax.swing.JTextField();
        quantitytxt = new javax.swing.JTextField();
        Datetxt = new javax.swing.JTextField();
        saveDeliBtn = new javax.swing.JButton();
        updDeliBtn = new javax.swing.JButton();
        DeleteDeliBtn = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        expDeliBtn = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ItemBtn.setText("Item Record");
        ItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemBtnActionPerformed(evt);
            }
        });

        DeliverBtn.setText("Supplier Record");
        DeliverBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeliverBtnActionPerformed(evt);
            }
        });

        SupplierBtn.setText("Deliver Record");
        SupplierBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SupplierBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ItemBtn)
                .addGap(173, 173, 173)
                .addComponent(DeliverBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(190, 190, 190)
                .addComponent(SupplierBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(146, 146, 146))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ItemBtn)
                    .addComponent(DeliverBtn)
                    .addComponent(SupplierBtn))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        ItemIDtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemIDtxtActionPerformed(evt);
            }
        });

        itemCodeTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemCodeTxtActionPerformed(evt);
            }
        });

        ItemNameTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemNameTxtActionPerformed(evt);
            }
        });

        ItemcategoryTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ItemcategoryTxtActionPerformed(evt);
            }
        });

        SaveItemBtn.setText("SAVE");
        SaveItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveItemBtnActionPerformed(evt);
            }
        });

        updItemBtn.setText("UPDATE");
        updItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updItemBtnActionPerformed(evt);
            }
        });

        DeleteItemBtn.setText("DELETE");
        DeleteItemBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DltItemBtnActionPerformed(evt);
            }
        });

        jLabel7.setText("Item Record");

        jLabel8.setText("Category");

        jLabel9.setText("Item Name");

        jLabel10.setText("Item Code");

        jLabel11.setText("Item ID");

        expitembtn.setText("EXPORT");
        expitembtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expitembtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ItemPanelLayout = new javax.swing.GroupLayout(ItemPanel);
        ItemPanel.setLayout(ItemPanelLayout);
        ItemPanelLayout.setHorizontalGroup(
            ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ItemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ItemPanelLayout.createSequentialGroup()
                        .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ItemcategoryTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(ItemNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8)
                            .addComponent(itemCodeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10)
                            .addComponent(ItemIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(ItemPanelLayout.createSequentialGroup()
                                .addGap(11, 11, 11)
                                .addComponent(SaveItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(updItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(DeleteItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 904, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(ItemPanelLayout.createSequentialGroup()
                        .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(expitembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 9, Short.MAX_VALUE))
        );
        ItemPanelLayout.setVerticalGroup(
            ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ItemPanelLayout.createSequentialGroup()
                .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ItemPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel11)
                        .addGap(4, 4, 4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ItemPanelLayout.createSequentialGroup()
                        .addComponent(expitembtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(ItemPanelLayout.createSequentialGroup()
                        .addComponent(ItemIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ItemNameTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(itemCodeTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ItemcategoryTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addGroup(ItemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SaveItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DeleteItemBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(161, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Item Record", ItemPanel);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Supplier ID", "Suppler Full Name", "Address", "Phone Number"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);

        SupplierIDtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SupplierIDtxtActionPerformed(evt);
            }
        });

        Addresstxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddresstxtActionPerformed(evt);
            }
        });

        SFullNametxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SFullNametxtActionPerformed(evt);
            }
        });

        Phonetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PhonetxtActionPerformed(evt);
            }
        });

        SaveSupBtn.setText("SAVE");
        SaveSupBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveSupBtnActionPerformed(evt);
            }
        });

        updSupplierBtn.setText("UPDATE");
        updSupplierBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updSupplierBtnActionPerformed(evt);
            }
        });

        deleteSupBtn.setText("DELETE");
        deleteSupBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSupBtnActionPerformed(evt);
            }
        });

        jLabel12.setText("Supplier Record");

        jLabel13.setText("Supplier ID");

        jLabel14.setText("Supplier Full Name");

        jLabel15.setText("Address");

        jLabel16.setText("Phone Number");

        expSupbtn.setText("EXPORT");

        javax.swing.GroupLayout SupplierPanelLayout = new javax.swing.GroupLayout(SupplierPanel);
        SupplierPanel.setLayout(SupplierPanelLayout);
        SupplierPanelLayout.setHorizontalGroup(
            SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SupplierPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Addresstxt, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SFullNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SupplierIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addGroup(SupplierPanelLayout.createSequentialGroup()
                        .addComponent(SaveSupBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updSupplierBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteSupBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 901, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(expSupbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        SupplierPanelLayout.setVerticalGroup(
            SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SupplierPanelLayout.createSequentialGroup()
                .addGroup(SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SupplierPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel13))
                    .addGroup(SupplierPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(expSupbtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(SupplierPanelLayout.createSequentialGroup()
                        .addComponent(SupplierIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)
                        .addGap(5, 5, 5)
                        .addComponent(SFullNametxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addGap(4, 4, 4)
                        .addComponent(Addresstxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel16)
                        .addGap(2, 2, 2)
                        .addComponent(Phonetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(62, 62, 62)
                        .addGroup(SupplierPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(SaveSupBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updSupplierBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteSupBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 401, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(169, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Supplier Record", SupplierPanel);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Delivery ID", "Supplier ID", "Item ID", "Consignor", "Supplier Name", "Item Name", "Quantity", "Price", "Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        DeliverIDtxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeliverIDtxtActionPerformed(evt);
            }
        });

        Consignortxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConsignortxtActionPerformed(evt);
            }
        });

        Pricnetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PricnetxtActionPerformed(evt);
            }
        });

        quantitytxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantitytxtActionPerformed(evt);
            }
        });

        Datetxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DatetxtActionPerformed(evt);
            }
        });

        saveDeliBtn.setText("SAVE");
        saveDeliBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDeliBtnActionPerformed(evt);
            }
        });

        updDeliBtn.setText("UPDATE");
        updDeliBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updDeliBtnActionPerformed(evt);
            }
        });

        DeleteDeliBtn.setText("DELETE");
        DeleteDeliBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DeleteDeliBtnActionPerformed(evt);
            }
        });

        jLabel1.setText("Deliver Record");

        jLabel2.setText("Delivery ID");

        jLabel3.setText("Supplier ID");

        jLabel4.setText("Item ID");

        jLabel5.setText("Consignor");

        jLabel6.setText("Supplier Full Name");

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel17.setText("Date");

        jLabel18.setText("Item Name");

        jLabel19.setText("Price");

        jLabel20.setText("Quantity");

        expDeliBtn.setText("EXPORT");

        javax.swing.GroupLayout DeliverPanelLayout = new javax.swing.GroupLayout(DeliverPanel);
        DeliverPanel.setLayout(DeliverPanelLayout);
        DeliverPanelLayout.setHorizontalGroup(
            DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4)
                                .addComponent(Pricnetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6)
                                .addComponent(Datetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(quantitytxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel3)
                            .addComponent(DeliverIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Consignortxt, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18)
                            .addComponent(jLabel17)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE)
                                .addComponent(jTextField4, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 912, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(expDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(72, 72, 72))
            .addGroup(DeliverPanelLayout.createSequentialGroup()
                .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addComponent(saveDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(DeleteDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        DeliverPanelLayout.setVerticalGroup(
            DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DeliverPanelLayout.createSequentialGroup()
                .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(expDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(DeliverPanelLayout.createSequentialGroup()
                        .addComponent(DeliverIDtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Consignortxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Pricnetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quantitytxt, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Datetxt, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel20)
                        .addGap(4, 4, 4)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(DeliverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(updDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DeleteDeliBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Deliver Record", DeliverPanel);

        jLabel21.setFont(new java.awt.Font("Segoe UI Historic", 0, 48)); // NOI18N
        jLabel21.setText("SUPPLIER MONITORING");

        jLayeredPane1.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel21, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 790, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(209, 209, 209))
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1193, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(375, 375, 375)
                        .addComponent(jLabel21)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 653, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1186, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(86, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 25, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void DeliverBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeliverBtnActionPerformed
       jTabbedPane1.setSelectedIndex(1); // Supplier Panel
        loadTableData(SUPPLIERS_TABLE, jTable3);
    }//GEN-LAST:event_DeliverBtnActionPerformed

    private void ItemNameTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemNameTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ItemNameTxtActionPerformed

    private void SupplierBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SupplierBtnActionPerformed
       jTabbedPane1.setSelectedIndex(2); // Deliver Panel
        loadTableData(DELIVERIES_TABLE, jTable2);
    }//GEN-LAST:event_SupplierBtnActionPerformed

    private void SaveItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveItemBtnActionPerformed
    String code = itemCodeTxt.getText().trim();
    String name = ItemNameTxt.getText().trim();
    String category = ItemcategoryTxt.getText().trim();

    if (code.isEmpty() || name.isEmpty() || category.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All Item fields are required.", 
                                      "Input Error", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Items newItem = new Items(null, code, name, category);
    String result = newItem.insertToDatabase_Item();

    JOptionPane.showMessageDialog(this, result, "Save Status", 
                                  result.startsWith("✓") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    
    loadItemsTable();
    clearItemFields(); 

    }//GEN-LAST:event_SaveItemBtnActionPerformed

    private void itemCodeTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemCodeTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_itemCodeTxtActionPerformed

    private void ItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemBtnActionPerformed
    jTabbedPane1.setSelectedIndex(0); // Item Panel
        loadTableData(ITEMS_TABLE, jTable1);
    }//GEN-LAST:event_ItemBtnActionPerformed

    private void PricnetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PricnetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PricnetxtActionPerformed

    private void saveDeliBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDeliBtnActionPerformed
  
        
    }//GEN-LAST:event_saveDeliBtnActionPerformed

    private void ItemcategoryTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemcategoryTxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ItemcategoryTxtActionPerformed

    private void ItemIDtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ItemIDtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ItemIDtxtActionPerformed

    private void PhonetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PhonetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_PhonetxtActionPerformed

    private void exportDeliBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportDeliBtnActionPerformed

}
private void DeleteDeliBtnActionPerformed(java.awt.event.ActionEvent evt) {
   
    }//GEN-LAST:event_exportDeliBtnActionPerformed

    private void SaveSupBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveSupBtnActionPerformed
   
    }//GEN-LAST:event_SaveSupBtnActionPerformed


    private void updSupplierBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updSupplierBtnActionPerformed
   
    }//GEN-LAST:event_updSupplierBtnActionPerformed

    private void exportSupBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportSupBtnActionPerformed

    }//GEN-LAST:event_exportSupBtnActionPerformed


    private void deleteSupBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSupBtnActionPerformed
    
    }//GEN-LAST:event_deleteSupBtnActionPerformed

    private void DeleteDeliBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeleteDeliBtnActionPerformed
    
    }//GEN-LAST:event_DeleteDeliBtnActionPerformed

    private void updItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updItemBtnActionPerformed
   String itemID = ItemIDtxt.getText().trim();
    String newCode = itemCodeTxt.getText().trim();
    String newName = ItemNameTxt.getText().trim();
    String newCategory = ItemcategoryTxt.getText().trim();

    if (itemID.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Select an item from the table to update.", 
                                      "Update Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String result = Items.updateItemInDatabase(itemID, newCode, newName, newCategory);

    JOptionPane.showMessageDialog(this, result, "Update Status", 
                                  result.startsWith("✓") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
    
    loadItemsTable();
    clearItemFields();
    }//GEN-LAST:event_updItemBtnActionPerformed

    private void ExportItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExportItemBtnActionPerformed
    }
private void DeleteItemBtnActionPerformed(java.awt.event.ActionEvent evt) {
    }//GEN-LAST:event_ExportItemBtnActionPerformed

    private void DltItemBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DltItemBtnActionPerformed
    String itemID = ItemIDtxt.getText().trim();

    if (itemID.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Select an item from the table to delete.", 
                                      "Delete Error", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete Item ID: " + itemID + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        // Calls the DAO delete method
        String result = Items.deleteItemFromDatabase(itemID);

        JOptionPane.showMessageDialog(this, result, "Delete Status", 
                                      result.startsWith("✓") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        
        loadItemsTable();
        clearItemFields();
    }//GEN-LAST:event_DltItemBtnActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void SupplierIDtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SupplierIDtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SupplierIDtxtActionPerformed

    private void SFullNametxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SFullNametxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_SFullNametxtActionPerformed

    private void AddresstxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AddresstxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AddresstxtActionPerformed

    private void DeliverIDtxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DeliverIDtxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DeliverIDtxtActionPerformed

    private void ConsignortxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConsignortxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ConsignortxtActionPerformed

    private void quantitytxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quantitytxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_quantitytxtActionPerformed

    private void DatetxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DatetxtActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DatetxtActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void expitembtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expitembtnActionPerformed
     exportTableToCSV(jTable1, "Items_Export.csv"); // TODO add your handling code here:
    }//GEN-LAST:event_expitembtnActionPerformed
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Addresstxt;
    private javax.swing.JTextField Consignortxt;
    private javax.swing.JTextField Datetxt;
    private javax.swing.JButton DeleteDeliBtn;
    private javax.swing.JButton DeleteItemBtn;
    private javax.swing.JButton DeliverBtn;
    private javax.swing.JTextField DeliverIDtxt;
    private javax.swing.JPanel DeliverPanel;
    private javax.swing.JButton ItemBtn;
    private javax.swing.JTextField ItemIDtxt;
    private javax.swing.JTextField ItemNameTxt;
    private javax.swing.JPanel ItemPanel;
    private javax.swing.JTextField ItemcategoryTxt;
    private javax.swing.JTextField Phonetxt;
    private javax.swing.JTextField Pricnetxt;
    private javax.swing.JTextField SFullNametxt;
    private javax.swing.JButton SaveItemBtn;
    private javax.swing.JButton SaveSupBtn;
    private javax.swing.JButton SupplierBtn;
    private javax.swing.JTextField SupplierIDtxt;
    private javax.swing.JPanel SupplierPanel;
    private javax.swing.JButton deleteSupBtn;
    private javax.swing.JButton expDeliBtn;
    private javax.swing.JButton expSupbtn;
    private javax.swing.JButton expitembtn;
    private javax.swing.JTextField itemCodeTxt;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField quantitytxt;
    private javax.swing.JButton saveDeliBtn;
    private javax.swing.JButton updDeliBtn;
    private javax.swing.JButton updItemBtn;
    private javax.swing.JButton updSupplierBtn;
    // End of variables declaration//GEN-END:variables
}
