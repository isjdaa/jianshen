package com.hello.dao;

import com.hello.entity.Customer;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public PagerVO<Customer> page(int current, int size, String whereSql) {
        PagerVO<Customer> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_customer" + whereSql);
        try {
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);
            
            resultSet = helper.executeQuery("select * from tb_customer " + whereSql + " limit " + ((current - 1) * size) + "," + size);
            List<Customer> list = new ArrayList<>();
            while (resultSet.next()) {
                Customer customer = toEntity(resultSet);
                list.add(customer);
            }
            pagerVO.setList(list);
            return pagerVO;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }
    
    public int insert(Customer customer) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
            "insert into tb_customer values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            customer.getId(), customer.getPassword(), customer.getName(), customer.getTele(),
            customer.getJoindate(), customer.getAge(), customer.getGender(), customer.getAddress(),
            customer.getMembershipType(), customer.getExpiryDate(), customer.getBalance(), customer.getCoachId(),
            customer.getAvatar()
        );
        helper.closeDB();
        return res;
    }
    
    public int update(Customer customer) {
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_customer set";
        List<Object> params = new ArrayList<>();
        
        if (customer.getPassword() != null) {
            sql += " password=?, ";
            params.add(customer.getPassword());
        }
        if (customer.getName() != null) {
            sql += " name=?, ";
            params.add(customer.getName());
        }
        if (customer.getTele() != null) {
            sql += " tele=?, ";
            params.add(customer.getTele());
        }
        if (customer.getJoindate() != null) {
            sql += " joindate=?, ";
            params.add(customer.getJoindate());
        }
        if (customer.getAge() != null) {
            sql += " age=?, ";
            params.add(customer.getAge());
        }
        if (customer.getGender() != null) {
            sql += " gender=?, ";
            params.add(customer.getGender());
        }
        if (customer.getAddress() != null) {
            sql += " address=?, ";
            params.add(customer.getAddress());
        }
        if (customer.getMembershipType() != null) {
            sql += " membership_type=?, ";
            params.add(customer.getMembershipType());
        }
        if (customer.getExpiryDate() != null) {
            sql += " expiry_date=?, ";
            params.add(customer.getExpiryDate());
        }
        if (customer.getBalance() != null) {
            sql += " balance=?, ";
            params.add(customer.getBalance());
        }
        if (customer.getCoachId() != null) {
            sql += " coach_id=?, ";
            params.add(customer.getCoachId());
        }
        if (customer.getAvatar() != null) {
            sql += " avatar=?, ";
            params.add(customer.getAvatar());
        } else {
            sql += " avatar=?, ";
            params.add(null);
        }
        
        sql = sql.substring(0, sql.length() - 2);
        sql += " where id = ?";
        params.add(customer.getId());
        
        res = helper.executeUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }
    
    public int delete(String id) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate("delete from tb_customer where id = ?", id);
        helper.closeDB();
        return res;
    }
    
    public Customer getById(String id) {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_customer where id = ?", id);
        try {
            if (resultSet.next()) {
                return toEntity(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return null;
    }
    
    public int count(String wheresql) {
        if (wheresql == null) {
            wheresql = "";
        }
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_customer" + wheresql);
        try {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return 0;
    }
    
    public int count() {
        return count("");
    }
    
    private Customer toEntity(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.setId(resultSet.getString("id"));
        customer.setPassword(resultSet.getString("password"));
        customer.setName(resultSet.getString("name"));
        customer.setTele(resultSet.getString("tele"));
        customer.setJoindate(resultSet.getDate("joindate"));
        customer.setAge(resultSet.getInt("age"));
        customer.setGender(resultSet.getString("gender"));
        customer.setAddress(resultSet.getString("address"));
        customer.setMembershipType(resultSet.getString("membership_type"));
        customer.setExpiryDate(resultSet.getDate("expiry_date"));
        customer.setBalance(resultSet.getDouble("balance"));
        customer.setCoachId(resultSet.getString("coach_id"));
        // 处理avatar字段可能不存在的情况
        customer.setAvatar(null); // 先初始化null，避免可能的默认值问题
        try {
            String avatar = resultSet.getString("avatar");
            if (avatar != null && !avatar.isEmpty()) {
                customer.setAvatar(avatar);
            }
        } catch (SQLException e) {
            // 如果avatar字段不存在，忽略此异常
            System.out.println("Warning: Avatar field not found in customer table");
        }
        return customer;
    }
}