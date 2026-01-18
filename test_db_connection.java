import java.sql.*;

public class test_db_connection {
    public static void main(String[] args) {
        String className = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/stu_manage?serverTimezone=GMT%2B8&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false";
        String user = "root";
        String pass = "123456";
        
        System.out.println("Testing database connection...");
        
        try {
            // Load driver
            System.out.println("Loading driver: " + className);
            Class.forName(className);
            System.out.println("Driver loaded successfully");
            
            // Establish connection
            System.out.println("Connecting to database: " + url);
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connection successful");
            
            // Test query
            System.out.println("Executing test query...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            if (rs.next()) {
                System.out.println("Query executed successfully, result: " + rs.getInt(1));
            }
            
            // Check tables
            System.out.println("Checking tables...");
            DatabaseMetaData meta = conn.getMetaData();
            
            // Check tb_clazz
            rs = meta.getTables(null, null, "tb_clazz", new String[] {"TABLE"});
            if (rs.next()) {
                System.out.println("Table tb_clazz exists");
            } else {
                System.out.println("Table tb_clazz does not exist");
            }
            
            // Check tb_student
            rs = meta.getTables(null, null, "tb_student", new String[] {"TABLE"});
            if (rs.next()) {
                System.out.println("Table tb_student exists");
            } else {
                System.out.println("Table tb_student does not exist");
            }
            
            // Close resources
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("Database connection closed");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Driver loading failed: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database operation failed: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Other error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Database connection test completed");
    }
}