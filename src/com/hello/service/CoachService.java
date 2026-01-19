package com.hello.service;

import com.hello.dao.CoachDAO;
import com.hello.entity.Coach;
import com.hello.utils.vo.PagerVO;

public class CoachService {

    CoachDAO dao = new CoachDAO();

    public String insert(Coach coach) {
        if (coach.getId() == null || coach.getId().equals("")) {
            return "教练编号不可为空";
        }
        if (coach.getPassword() == null || coach.getPassword().equals("")) {
            return "密码不可为空";
        }
        if (coach.getName() == null || coach.getName().equals("")) {
            return "教练姓名不可为空";
        }
        Coach ex = dao.getById(coach.getId());
        if (ex != null) {
            return "教练编号已存在！";
        }
        dao.insert(coach);
        return null;
    }
    
    public String update(Coach coach) {
        if (coach.getId() == null || coach.getId().equals("")) {
            return "被修改的教练编号不可为空";
        }
        dao.update(coach);
        return null;
    }

    public Coach getById(String id) {
        return dao.getById(id);
    }
    
    public int count() {
        return dao.count();
    }

    public PagerVO<Coach> page(int current, int size, String id, String name, String gender, String specialization) {
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
        if (specialization != null && !"" .equals(specialization)) {
            whereSql += " and specialization like '%" + specialization + "%'";
        }
        return dao.page(current, size, whereSql);
    }

    public int delete(String id) {
        return dao.delete(id);
    }
}