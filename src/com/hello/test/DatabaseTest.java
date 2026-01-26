package com.hello.test;

import com.hello.utils.JdbcHelper;

import java.util.List;
import java.util.Map;

public class DatabaseTest {
    public static void main(String[] args) {
        System.out.println("=== Testing database connection and tb_course table structure ===");
        
        JdbcHelper jdbcHelper = new JdbcHelper();
        
        try {
            // Test connection
            System.out.println("Database connection successful!");
            
            // Test if tb_course table exists
            String checkTableSql = "SHOW TABLES LIKE 'tb_course'";
            List<Map<String, Object>> tableResult = jdbcHelper.executeQueryToList(checkTableSql);
            System.out.println("tb_course table exists: " + !tableResult.isEmpty());
            
            // Test tb_course table structure
            if (!tableResult.isEmpty()) {
                String describeSql = "DESCRIBE tb_course";
                List<Map<String, Object>> structureResult = jdbcHelper.executeQueryToList(describeSql);
                System.out.println("tb_course table structure:");
                for (Map<String, Object> row : structureResult) {
                    System.out.println(row.get("Field") + " - " + row.get("Type"));
                }
                
                // Test querying data
                String dataSql = "SELECT * FROM tb_course LIMIT 5";
                List<Map<String, Object>> dataResult = jdbcHelper.executeQueryToList(dataSql);
                System.out.println("\ntb_course table data (first 5 rows):");
                for (Map<String, Object> row : dataResult) {
                    System.out.println("ID: " + row.get("id") + ", Course Name: " + row.get("course_name") + ", Course Time: " + row.get("course_time"));
                }
                
                // Test admin query SQL
                String adminSql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.course_time IS NOT NULL ORDER BY c.course_time DESC LIMIT 10";
                List<Map<String, Object>> adminResult = jdbcHelper.executeQueryToList(adminSql);
                System.out.println("\nAdmin query result (first 10 rows):");
                System.out.println("Number of records found: " + adminResult.size());
                for (Map<String, Object> row : adminResult) {
                    System.out.println("ID: " + row.get("id") + ", Course Name: " + row.get("course_name") + ", Coach Name: " + row.get("coach_name") + ", Course Time: " + row.get("course_time"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Test failed: " + e.getMessage());
        } finally {
            if (jdbcHelper != null) {
                jdbcHelper.closeDB();
            }
        }
        
        System.out.println("=== Test completed ===");
    }
}
