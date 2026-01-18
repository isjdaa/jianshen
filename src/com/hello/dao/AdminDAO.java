package com.hello.dao;

import com.hello.entity.Admin;
import com.hello.utils.JdbcHelper;

import java.sql.ResultSet;

public class AdminDAO{

    public Admin getByUsername(String username){
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_admin where username = ?",username);
        try {
            if(resultSet.next()){
                Admin admin = new Admin();
                admin.setUsername( resultSet.getString("username"));
                admin.setPassword( resultSet.getString("password"));
                return admin;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            helper.closeDB();
        }
        return null;
    }

}
