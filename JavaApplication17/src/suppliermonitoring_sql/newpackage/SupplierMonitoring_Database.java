package suppliermonitoring_sql.newpackage;
import java.sql.*;

public class SupplierMonitoring_Database {
    
    private static final String url = "jdbc:mysql://localhost:3306/supplier_monitoring";
    private static final String user = "root";
    private static final String pass = "shairamata"; // leave blank if no password
    
    public static Connection getConnection() {
        Connection con = null;
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Database connection error: " + e.getMessage());
        }
        return con;
    }
}
