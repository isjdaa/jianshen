package com.hello.service;

import com.hello.dao.AdminDAO;
import com.hello.entity.Admin;

public class AdminService {
    AdminDAO adminDao = new AdminDAO();


    public static void main(String[] args) {
        AdminService service=new AdminService();
        Admin student=service.getByUsername("admin");
        System.out.println(student);
    }
    public Admin getByUsername(String username){
        return adminDao.getByUsername(username);
    }
}

