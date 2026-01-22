import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InsertTestData {
    public static void main(String[] args) {
        // Database connection information
        String url = "jdbc:mysql://localhost:3306/stu_manage?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC";
        String username = "root";
        String password = "123456";
        
        Connection conn = null;
        Statement stmt = null;
        
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish database connection
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            
            // Read SQL script file
            String sqlFile = "d:\\jianshen\\db\\migration\\005_insert_test_data.sql";
            BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
            String line;
            StringBuilder sql = new StringBuilder();
            
            // Execute SQL script
            System.out.println("Start executing SQL script...");
            while ((line = reader.readLine()) != null) {
                // Skip comments and empty lines
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }
                
                // Accumulate SQL statements
                sql.append(line);
                
                // Execute SQL when encountering semicolon
                if (line.endsWith(";")) {
                    stmt.execute(sql.toString());
                    System.out.println("Executed SQL: " + sql.toString());
                    sql.setLength(0);
                }
            }
            
            reader.close();
            System.out.println("SQL script execution completed!");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}