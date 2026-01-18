package com.hello.utils;

import com.alibaba.fastjson.parser.deserializer.SqlDateDeserializer;

import java.sql.*;

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public  ResultSet executeQuery(String sql,Object... params){
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i+1, params[i]);
                };
            }
            rs=pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }
    public  int executeUpdate(String sql,Object... params){
        int row=1;
        try {
            pstmt=conn.prepareStatement(sql);
            if(params!=null){
                for(int i=0; i<params.length;i++) {
                    pstmt.setObject(i + 1, params[i]);
                };
            }
            row=pstmt.executeUpdate();//mysql执行后影响的行数
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }
    public void closeDB(){
        if(rs!=null){
            try{
            rs.close();
        }catch (SQLException throwables){
                throwables.printStackTrace();
            }
if(pstmt!=null){
    try{
        pstmt.close();
    }catch(SQLException throwables){
        throwables.printStackTrace();
    }
}
if(conn!=null){
    try{
        conn.close();
    }catch (SQLException throwables){
        throwables.printStackTrace();
    }

}
        }
    }
}
