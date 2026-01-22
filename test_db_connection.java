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

            // Check check_in_record
            rs = meta.getTables(null, null, "check_in_record", new String[] {"TABLE"});
            if (rs.next()) {
                System.out.println("Table check_in_record exists");
            } else {
                System.out.println("Table check_in_record does not exist");
            }

            // Check tb_course
            rs = meta.getTables(null, null, "tb_course", new String[] {"TABLE"});
            if (rs.next()) {
                System.out.println("Table tb_course exists");
            } else {
                System.out.println("Table tb_course does not exist");
            }

            // Update COACH003 course time to current time
            System.out.println("Updating COACH003 course time to current time...");
            Statement updateStmt = conn.createStatement();
            int updatedRows = updateStmt.executeUpdate("UPDATE tb_course SET course_time = NOW() WHERE id = 'COURSE_TEST_003'");
            System.out.println("Updated " + updatedRows + " rows for COACH003");

            // Check if check_in_record has data
            System.out.println("Checking if check_in_record has data...");
            Statement countStmt = conn.createStatement();
            ResultSet countRs = countStmt.executeQuery("SELECT COUNT(*) FROM check_in_record");
            if (countRs.next()) {
                System.out.println("check_in_record has " + countRs.getInt(1) + " records");
            }
            countRs.close();
            countStmt.close();

            // Check check_in_record table structure
            System.out.println("Checking check_in_record table structure...");
            Statement descStmt = conn.createStatement();
            ResultSet descRs = descStmt.executeQuery("DESCRIBE check_in_record");
            while (descRs.next()) {
                System.out.println("Column: " + descRs.getString("Field") + " - Type: " + descRs.getString("Type") + " - Null: " + descRs.getString("Null"));
            }
            descRs.close();
            descStmt.close();

            // Fix check_in_record table structure if needed
            System.out.println("Fixing check_in_record table structure...");
            Statement fixStmt = conn.createStatement();

            // Drop existing table
            fixStmt.executeUpdate("DROP TABLE IF EXISTS check_in_record");

            // Create table with correct structure
            fixStmt.executeUpdate("CREATE TABLE check_in_record (" +
                "id VARCHAR(64) PRIMARY KEY, " +
                "coach_id VARCHAR(64) NOT NULL, " +
                "coach_name VARCHAR(64) NOT NULL, " +
                "check_type VARCHAR(16) NOT NULL, " +
                "check_time DATETIME NOT NULL, " +
                "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")");

            // Create indexes
            fixStmt.executeUpdate("CREATE INDEX idx_checkin_coach ON check_in_record(coach_id)");
            fixStmt.executeUpdate("CREATE INDEX idx_checkin_time ON check_in_record(check_time)");
            fixStmt.executeUpdate("CREATE INDEX idx_checkin_type ON check_in_record(check_type)");

            System.out.println("check_in_record table recreated with correct structure");
            fixStmt.close();

            // Check current courses for all coaches
            System.out.println("Checking current courses for all coaches...");
            Statement courseStmt = conn.createStatement();
            ResultSet courseRs = courseStmt.executeQuery("SELECT coach_id, id, course_name, course_time, status FROM tb_course WHERE course_time BETWEEN DATE_SUB(NOW(), INTERVAL 1 HOUR) AND DATE_ADD(NOW(), INTERVAL 1 HOUR) ORDER BY coach_id");
            while (courseRs.next()) {
                System.out.println("Coach: " + courseRs.getString("coach_id") + " - Course: " + courseRs.getString("id") + " - " + courseRs.getString("course_name") + " - " + courseRs.getTimestamp("course_time") + " - " + courseRs.getString("status"));
            }
            courseRs.close();
            courseStmt.close();
            updateStmt.close();
            
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