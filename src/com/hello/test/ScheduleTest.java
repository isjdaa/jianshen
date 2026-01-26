package com.hello.test;

import com.hello.utils.JdbcHelper;

import java.util.List;
import java.util.Map;

public class ScheduleTest {
    public static void main(String[] args) {
        System.out.println("=== Testing schedule data for admin ===");
        
        JdbcHelper jdbcHelper = new JdbcHelper();
        
        try {
            // Test admin schedule query
            String adminSql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.course_time IS NOT NULL ORDER BY c.course_time DESC";
            List<Map<String, Object>> adminResult = jdbcHelper.executeQueryToList(adminSql);
            
            System.out.println("Admin query SQL: " + adminSql);
            System.out.println("Number of records found: " + adminResult.size());
            
            if (adminResult.isEmpty()) {
                System.out.println("No schedule records found!");
                
                // Check if tb_course table exists
                String checkTableSql = "SHOW TABLES LIKE 'tb_course'";
                List<Map<String, Object>> tableResult = jdbcHelper.executeQueryToList(checkTableSql);
                System.out.println("tb_course table exists: " + !tableResult.isEmpty());
                
                // Check tb_course table structure
                if (!tableResult.isEmpty()) {
                    String describeSql = "DESCRIBE tb_course";
                    List<Map<String, Object>> structureResult = jdbcHelper.executeQueryToList(describeSql);
                    System.out.println("tb_course table structure:");
                    for (Map<String, Object> row : structureResult) {
                        System.out.println(row.get("Field") + " - " + row.get("Type"));
                    }
                    
                    // Check all records in tb_course
                    String allRecordsSql = "SELECT * FROM tb_course";
                    List<Map<String, Object>> allRecords = jdbcHelper.executeQueryToList(allRecordsSql);
                    System.out.println("All records in tb_course: " + allRecords.size());
                    for (Map<String, Object> row : allRecords) {
                        System.out.println("ID: " + row.get("id") + ", Course Name: " + row.get("course_name") + ", Course Time: " + row.get("course_time"));
                    }
                }
            } else {
                System.out.println("Schedule records found:");
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