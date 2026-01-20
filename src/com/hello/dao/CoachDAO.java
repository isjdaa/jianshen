package com.hello.dao;

import com.hello.entity.Coach;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoachDAO {

    public PagerVO<Coach> page(int current, int size, String whereSql) {
        PagerVO<Coach> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_coach" + whereSql);
        try {
            resultSet.next();
            int total = resultSet.getInt(1);
            pagerVO.setTotal(total);
            
            resultSet = helper.executeQuery("select * from tb_coach " + whereSql + " limit " + ((current - 1) * size) + "," + size);
            List<Coach> list = new ArrayList<>();
            while (resultSet.next()) {
                Coach coach = toEntity(resultSet);
                list.add(coach);
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
    
    public int insert(Coach coach) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
            "insert into tb_coach values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
            coach.getId(), coach.getPassword(), coach.getName(), coach.getTele(),
            coach.getSpecialization(), coach.getGender(), coach.getAge(), coach.getAddress(),
            coach.getAvatar()
        );
        helper.closeDB();
        return res;
    }
    
    public int update(Coach coach) {
        JdbcHelper helper = new JdbcHelper();
        int res = 0;
        String sql = "update tb_coach set";
        List<Object> params = new ArrayList<>();
        
        if (coach.getPassword() != null) {
            sql += " password=?, ";
            params.add(coach.getPassword());
        }
        if (coach.getName() != null) {
            sql += " name=?, ";
            params.add(coach.getName());
        }
        if (coach.getTele() != null) {
            sql += " tele=?, ";
            params.add(coach.getTele());
        }
        if (coach.getSpecialization() != null) {
            sql += " specialization=?, ";
            params.add(coach.getSpecialization());
        }
        if (coach.getGender() != null) {
            sql += " gender=?, ";
            params.add(coach.getGender());
        }
        if (coach.getAge() != null) {
            sql += " age=?, ";
            params.add(coach.getAge());
        }
        if (coach.getAddress() != null) {
            sql += " address=?, ";
            params.add(coach.getAddress());
        }
        if (coach.getAvatar() != null) {
            sql += " avatar=?, ";
            params.add(coach.getAvatar());
        }
        
        sql = sql.substring(0, sql.length() - 2);
        sql += " where id = ?";
        params.add(coach.getId());
        
        res = helper.executeUpdate(sql, params.toArray());
        helper.closeDB();
        return res;
    }
    
    public int delete(String id) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate("delete from tb_coach where id = ?", id);
        helper.closeDB();
        return res;
    }
    
    public Coach getById(String id) {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_coach where id = ?", id);
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
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_coach" + wheresql);
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
    
    private Coach toEntity(ResultSet resultSet) throws SQLException {
        Coach coach = new Coach();
        coach.setId(resultSet.getString("id"));
        coach.setPassword(resultSet.getString("password"));
        coach.setName(resultSet.getString("name"));
        coach.setTele(resultSet.getString("tele"));
        coach.setSpecialization(resultSet.getString("specialization"));
        coach.setGender(resultSet.getString("gender"));
        coach.setAge(resultSet.getInt("age"));
        coach.setAddress(resultSet.getString("address"));
        // 处理avatar字段可能不存在的情况
        try {
            coach.setAvatar(resultSet.getString("avatar"));
        } catch (SQLException e) {
            // 如果avatar字段不存在，忽略此异常
            coach.setAvatar(null);
        }
        return coach;
    }
}