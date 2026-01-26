package com.hello.utils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*1数据库配置信息       2提供基本的和数据库交互的方法*/
public class JdbcHelper {
    private static final String className = "com.mysql.cj.jdbc.Driver";
    private static final String url = "jdbc:mysql://localhost:3306/stu_manage?serverTimezone=GMT%2B8&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String user = "root";
    private static final String pass = "123456";

    static {
        try{
            Class.forName(className);
        } catch (ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection conn=null;
    private PreparedStatement pstmt=null;
    private ResultSet rs=null;

    public  JdbcHelper() {
        try {
            conn= DriverManager.getConnection(url,user,pass);
            // 强制开启自动提交，避免事务延迟
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ========== 原有方法（保留不变，供其他功能使用） ==========
    public List<Map<String, Object>> executeQueryToList(String sql,Object... params){
        List<Map<String, Object>> resultList = new ArrayList<>();
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i+1, params[i]);
                }
            }
            rs=pstmt.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(rs.next()){
                Map<String, Object> rowMap = new HashMap<>();
                for(int i=1; i<=columnCount; i++){
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    rowMap.put(columnName, columnValue);
                }
                resultList.add(rowMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
        return resultList;
    }

    public ResultSet executeQuery(String sql,Object... params){
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i+1, params[i]);
                }
            }
            rs=pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public  int executeUpdate(String sql,Object... params){
        int row=0;
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            row=pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeDB();
        }
        return row;
    }

    // ========== 新增方法（无自动关闭+显式提交，专供签到功能） ==========
    /**
     * 执行查询（不自动关闭连接）
     */
    public List<Map<String, Object>> executeQueryToListNoClose(String sql,Object... params){
        List<Map<String, Object>> resultList = new ArrayList<>();
        PreparedStatement localPstmt = null;
        ResultSet localRs = null;
        try {
            localPstmt = conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    localPstmt.setObject(i+1, params[i]);
                }
            }
            localRs = localPstmt.executeQuery();

            ResultSetMetaData metaData = localRs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while(localRs.next()){
                Map<String, Object> rowMap = new HashMap<>();
                for(int i=1; i<=columnCount; i++){
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = localRs.getObject(i);
                    rowMap.put(columnName, columnValue);
                }
                resultList.add(rowMap);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL查询执行失败: " + e.getMessage());
            System.out.println("SQL语句: " + sql);
        }
        // 不关闭连接！！！
        return resultList;
    }

    /**
     * 执行增删改（不自动关闭连接+显式提交，确保插入即时生效）
     */
    public  int executeUpdateNoClose(String sql,Object... params){
        int row=0;
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            row=pstmt.executeUpdate();
            // 关键：显式提交事务，强制插入立即生效
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
            }
            conn.setAutoCommit(true); // 重置自动提交
        } catch (SQLException e) {
            e.printStackTrace();
            // 异常回滚
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        // 不关闭连接！！！
        return row;
    }

    // 通用关闭方法（供调用方手动关闭）
    public void closeDB(){
        if(rs!=null){
            try{
                rs.close();
            }catch (SQLException throwables){
                throwables.printStackTrace();
            } finally {
                rs = null;
            }
        }
        if(pstmt!=null){
            try{
                pstmt.close();
            }catch(SQLException throwables){
                throwables.printStackTrace();
            } finally {
                pstmt = null;
            }
        }
        if(conn!=null){
            try{
                conn.close();
            }catch (SQLException throwables){
                throwables.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }
}