package com.hello.dao;

import com.hello.entity.CourseAppointment;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseAppointmentDAO {

    // 添加课程预约
    public int insert(CourseAppointment courseAppointment) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "insert into tb_course_appointment values(?, ?, ?, ?, ?, ?)",
                courseAppointment.getId(), courseAppointment.getCustomerId(), 
                courseAppointment.getCourseId(), courseAppointment.getAppointmentTime(),
                courseAppointment.getStatus(), new Date()
        );
        helper.closeDB();
        return res;
    }

    // 更新课程预约状态
    public int updateStatus(String id, String status) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "update tb_course_appointment set status = ? where id = ?",
                status, id
        );
        helper.closeDB();
        return res;
    }

    // 根据ID查询课程预约
    public CourseAppointment getById(String id) {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_course_appointment where id = ?", id);
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

    // 查询客户的课程预约列表
    public PagerVO<CourseAppointment> findByCustomerId(int current, int size, String customerId) {
        PagerVO<CourseAppointment> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        
        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_course_appointment where customer_id = ?", customerId);
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }
            
            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_course_appointment where customer_id = ? order by create_time desc limit ?, ?",
                    customerId, (current - 1) * size, size
            );
            List<CourseAppointment> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(toEntity(resultSet));
            }
            pagerVO.setList(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    // 查询课程的预约列表
    public PagerVO<CourseAppointment> findByCourseId(int current, int size, String courseId) {
        PagerVO<CourseAppointment> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        
        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_course_appointment where course_id = ?", courseId);
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }
            
            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_course_appointment where course_id = ? order by create_time desc limit ?, ?",
                    courseId, (current - 1) * size, size
            );
            List<CourseAppointment> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(toEntity(resultSet));
            }
            pagerVO.setList(list);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return pagerVO;
    }

    // 转换为实体对象
    private CourseAppointment toEntity(ResultSet resultSet) throws SQLException {
        CourseAppointment courseAppointment = new CourseAppointment();
        courseAppointment.setId(resultSet.getString("id"));
        courseAppointment.setCustomerId(resultSet.getString("customer_id"));
        courseAppointment.setCourseId(resultSet.getString("course_id"));
        courseAppointment.setAppointmentTime(resultSet.getTimestamp("appointment_time"));
        courseAppointment.setStatus(resultSet.getString("status"));
        courseAppointment.setCreateTime(resultSet.getTimestamp("create_time"));
        return courseAppointment;
    }
}