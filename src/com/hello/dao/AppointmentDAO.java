package com.hello.dao;

import com.hello.entity.Appointment;
import com.hello.utils.JdbcHelper;
import com.hello.utils.vo.PagerVO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentDAO {

    // 添加预约
    public int insert(Appointment appointment) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "insert into tb_appointment values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                appointment.getId(), appointment.getCustomerId(), appointment.getCoachId(),
                appointment.getAppointmentDate(), appointment.getAppointmentTime(),
                appointment.getStatus(), appointment.getCreateTime(),
                appointment.getUpdateTime(), appointment.getRemarks()
        );
        helper.closeDB();
        return res;
    }

    // 更新预约状态
    public int updateStatus(String id, String status) {
        JdbcHelper helper = new JdbcHelper();
        int res = helper.executeUpdate(
                "update tb_appointment set status = ?, update_time = ? where id = ?",
                status, new Date(), id
        );
        helper.closeDB();
        return res;
    }

    // 根据ID查询预约
    public Appointment getById(String id) {
        JdbcHelper helper = new JdbcHelper();
        ResultSet resultSet = helper.executeQuery("select * from tb_appointment where id = ?", id);
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

    // 检查客户是否已有相同时间的教练预约
    public boolean hasDuplicateAppointment(String customerId, String coachId, Date appointmentDate, String appointmentTime, String excludeId) {
        JdbcHelper helper = new JdbcHelper();
        String sql = "select count(1) from tb_appointment where customer_id = ? and coach_id = ? and appointment_date = ? and appointment_time = ? and status in ('pending', 'confirmed')";
        if (excludeId != null && !excludeId.isEmpty()) {
            sql += " and id != ?";
        }

        ResultSet resultSet;
        if (excludeId != null && !excludeId.isEmpty()) {
            resultSet = helper.executeQuery(sql, customerId, coachId, appointmentDate, appointmentTime, excludeId);
        } else {
            resultSet = helper.executeQuery(sql, customerId, coachId, appointmentDate, appointmentTime);
        }

        try {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return false;
    }

    // 检查教练在指定时间是否已有预约（包括待确认的预约，防止恶意占位）
    public boolean hasTimeConflict(String coachId, Date appointmentDate, String appointmentTime, String excludeId) {
        JdbcHelper helper = new JdbcHelper();
        String sql = "select count(1) from tb_appointment where coach_id = ? and appointment_date = ? and appointment_time = ? and status in ('pending', 'confirmed')";
        if (excludeId != null && !excludeId.isEmpty()) {
            sql += " and id != ?";
        }

        ResultSet resultSet;
        if (excludeId != null && !excludeId.isEmpty()) {
            resultSet = helper.executeQuery(sql, coachId, appointmentDate, appointmentTime, excludeId);
        } else {
            resultSet = helper.executeQuery(sql, coachId, appointmentDate, appointmentTime);
        }

        try {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            helper.closeDB();
        }
        return false;
    }

    // 查询客户的预约列表（支持分页）
    public PagerVO<Appointment> findByCustomerId(int current, int size, String customerId) {
        PagerVO<Appointment> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();

        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_appointment where customer_id = ?", customerId);
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }

            // 计算总页数
            int totalPages = (int) Math.ceil((double) pagerVO.getTotal() / size);
            pagerVO.setTotalPages(totalPages);

            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_appointment where customer_id = ? order by appointment_date desc, appointment_time desc limit ?, ?",
                    customerId, (current - 1) * size, size
            );
            List<Appointment> list = new ArrayList<>();
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

    // 查询教练的预约列表
    public PagerVO<Appointment> findByCoachId(int current, int size, String coachId) {
        PagerVO<Appointment> pagerVO = new PagerVO<>();
        pagerVO.setCurrent(current);
        pagerVO.setSize(size);
        JdbcHelper helper = new JdbcHelper();
        
        // 查询总数
        ResultSet resultSet = helper.executeQuery("select count(1) from tb_appointment where coach_id = ?", coachId);
        try {
            if (resultSet.next()) {
                pagerVO.setTotal(resultSet.getInt(1));
            }
            
            // 查询数据
            resultSet = helper.executeQuery(
                    "select * from tb_appointment where coach_id = ? order by appointment_date desc, appointment_time desc limit ?, ?",
                    coachId, (current - 1) * size, size
            );
            List<Appointment> list = new ArrayList<>();
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
    private Appointment toEntity(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(resultSet.getString("id"));
        appointment.setCustomerId(resultSet.getString("customer_id"));
        appointment.setCoachId(resultSet.getString("coach_id"));
        appointment.setAppointmentDate(resultSet.getTimestamp("appointment_date"));
        appointment.setAppointmentTime(resultSet.getString("appointment_time"));
        // 兼容数据库中的中文状态值，统一映射到前端/业务使用的英文状态码
        String status = resultSet.getString("status");
        if (status != null) {
            status = status.trim();
        }
        if ("待确认".equals(status)) {
            status = "pending";
        } else if ("已确认".equals(status)) {
            status = "confirmed";
        } else if ("已完成".equals(status)) {
            status = "completed";
        } else if ("已取消".equals(status)) {
            status = "cancelled";
        }
        appointment.setStatus(status);
        appointment.setCreateTime(resultSet.getTimestamp("create_time"));
        appointment.setUpdateTime(resultSet.getTimestamp("update_time"));
        appointment.setRemarks(resultSet.getString("remarks"));
        return appointment;
    }
}