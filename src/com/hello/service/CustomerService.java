package com.hello.service;

import com.hello.dao.CustomerDAO;
import com.hello.entity.Customer;
import com.hello.utils.vo.PagerVO;

public class CustomerService {

    CustomerDAO dao = new CustomerDAO();

    public String insert(Customer customer) {
        if (customer.getId() == null || customer.getId().equals("")) {
            return "客户编号不可为空";
        }
        if (customer.getPassword() == null || customer.getPassword().equals("")) {
            return "密码不可为空";
        }
        if (customer.getName() == null || customer.getName().equals("")) {
            return "客户姓名不可为空";
        }
        Customer ex = dao.getById(customer.getId());
        if (ex != null) {
            return "客户编号已存在！";
        }
        dao.insert(customer);
        return null;
    }
    
    public String update(Customer customer) {
        if (customer.getId() == null || customer.getId().equals("")) {
            return "被修改的客户编号不可为空";
        }
        dao.update(customer);
        return null;
    }

    public Customer getById(String id) {
        return dao.getById(id);
    }
    
    public int count() {
        return dao.count();
    }

    public PagerVO<Customer> page(int current, int size, String id, String name, String gender, String coachId) {
        String whereSql = " where 1=1 ";
        //拼接查询条件
        if (id != null && !"" .equals(id)) {
            whereSql += " and id like '%" + id + "%'";
        }
        if (name != null && !"" .equals(name)) {
            whereSql += " and name like '%" + name + "%'";
        }
        if (gender != null && !"" .equals(gender)) {
            whereSql += " and gender = '" + gender + "'";
        }
        if (coachId != null && !"" .equals(coachId)) {
            whereSql += " and coach_id = '" + coachId + "'";
        }
        return dao.page(current, size, whereSql);
    }

    public int delete(String id) {
        return dao.delete(id);
    }
}