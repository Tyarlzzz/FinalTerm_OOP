package suppliermonitoring_classes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/supplier_monitoring";
    private static final String USER = "root";
    private static final String PASSWORD = "ADMIN_@yoni0929"; // lagay ung own password nyo sa database
                                                              // then wag na i push ulit to sa github, since iba iba tayo password
    private static Connection connection = null;
    

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Database connected successfully!"); // remove or comment to once working na db connection, error handling purposes lang to
                } catch (ClassNotFoundException e) {
                    System.err.println("MySQL JDBC Driver not found. Please include it in your library path.");
                    e.printStackTrace();
                } catch (SQLException e) {
                    System.err.println("Database connection failed!"); // remove or comment to once working na db connection, error handling purposes lang to
                    System.err.println("Error Code: " + e.getErrorCode());
                    System.err.println("SQL State: " + e.getSQLState());
                    System.err.println("Message: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed."); // remove or comment to once working na db connection, error handling purposes lang to
            }
        } catch (SQLException e) {
            System.err.println("Failed to close database connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            System.out.println("Connection test successful!");
        } else {
            System.err.println("Connection test failed.");
        }
        DatabaseConnection.closeConnection();
    }
}