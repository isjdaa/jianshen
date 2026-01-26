import java.sql.*;

public class TestDatabase {
    private static final String url = "jdbc:mysql://localhost:3306/stu_manage?serverTimezone=GMT%2B8&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String user = "root";
    private static final String pass = "123456";
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully");
            
            Connection conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connected successfully");
            
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet tables = meta.getTables(null, null, "tb_course", new String[] {"TABLE"});
            if (tables.next()) {
                System.out.println("tb_course table exists");
                
                ResultSet columns = meta.getColumns(null, null, "tb_course", "%");
                System.out.println("\ntb_course table structure:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println(columnName + " : " + columnType);
                }
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM tb_course");
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("\ntb_course table has " + count + " records");
                }
                
                rs = stmt.executeQuery("SELECT * FROM tb_course LIMIT 10");
                System.out.println("\ntb_course table first 10 records:");
                while (rs.next()) {
                    String id = rs.getString("id");
                    String courseName = rs.getString("course_name");
                    String courseTime = rs.getString("course_time");
                    System.out.println(id + " | " + courseName + " | " + courseTime);
                }
                
                rs = stmt.executeQuery("SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id ORDER BY c.course_time DESC LIMIT 50");
                System.out.println("\nJOIN query result:");
                int joinCount = 0;
                while (rs.next()) {
                    joinCount++;
                    String courseName = rs.getString("course_name");
                    String coachName = rs.getString("coach_name");
                    System.out.println(joinCount + ". " + courseName + " | " + (coachName != null ? coachName : "No coach"));
                }
                System.out.println("JOIN query returned " + joinCount + " records");
                
            } else {
                System.out.println("tb_course table does not exist");
            }
            
            conn.close();
            System.out.println("\nDatabase connection closed");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}