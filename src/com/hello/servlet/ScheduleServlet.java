package com.hello.servlet;

import com.hello.entity.Coach;
import com.hello.utils.JdbcHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 排课查询Servlet
 */
@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 验证登录
        HttpSession session = request.getSession();
        Coach coach = (Coach) session.getAttribute("user");
        if (coach == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 获取查询类型
        String queryType = request.getParameter("queryType");
        List<ScheduleRecord> scheduleList = new ArrayList<>();

        JdbcHelper jdbcHelper = new JdbcHelper();
        ResultSet rs = null;

        try {
            String sql = "";
            Object[] params = null;

            // 根据查询类型拼接SQL
            if ("1".equals(queryType)) {
                // 今日排课
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());
                sql = "SELECT * FROM course_schedule WHERE schedule_date = ?";
                params = new Object[]{today};
            } else if ("2".equals(queryType)) {
                // 我的排课
                sql = "SELECT * FROM course_schedule WHERE coach_id = ?";
                params = new Object[]{coach.getId()};
            } else if ("3".equals(queryType)) {
                // 按日期查询
                String queryDate = request.getParameter("queryDate");
                if (queryDate == null || queryDate.isEmpty()) {
                    request.setAttribute("msg", "请输入查询日期！");
                    request.getRequestDispatcher("schedule.jsp").forward(request, response);
                    return;
                }
                sql = "SELECT * FROM course_schedule WHERE schedule_date = ?";
                params = new Object[]{queryDate};
            } else {
                request.setAttribute("msg", "请选择正确的查询类型！");
                request.getRequestDispatcher("schedule.jsp").forward(request, response);
                return;
            }

            // 执行查询
            rs = jdbcHelper.executeQuery(sql, params);

            // 处理结果集
            while (rs.next()) {
                ScheduleRecord record = new ScheduleRecord();
                record.setId(rs.getInt("id"));
                record.setScheduleDate(rs.getString("schedule_date"));
                record.setStartTime(rs.getString("start_time"));
                record.setEndTime(rs.getString("end_time"));
                record.setCourseName(rs.getString("course_name"));
                record.setCoachId(rs.getString("coach_id"));
                record.setCoachName(rs.getString("coach_name"));
                record.setLocation(rs.getString("location"));
                record.setMaxStudents(rs.getInt("max_students"));
                record.setCurrentStudents(rs.getInt("current_students"));
                record.setStatus(rs.getString("status"));
                scheduleList.add(record);
            }

            // 将查询结果存入request
            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("msg", "查询成功，共找到 " + scheduleList.size() + " 条记录");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("msg", "查询失败，数据库异常！");
        } finally {
            // 关闭资源
            jdbcHelper.closeDB();
        }

        request.getRequestDispatcher("schedule.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 验证登录
        HttpSession session = request.getSession();
        Coach coach = (Coach) session.getAttribute("user");
        if (coach == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 直接跳转到排课查询页面
        request.getRequestDispatcher("schedule.jsp").forward(request, response);
    }

    // 内部类：排课记录
    private static class ScheduleRecord {
        private int id;
        private String scheduleDate;
        private String startTime;
        private String endTime;
        private String courseName;
        private String coachId;
        private String coachName;
        private String location;
        private int maxStudents;
        private int currentStudents;
        private String status;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getScheduleDate() {
            return scheduleDate;
        }

        public void setScheduleDate(String scheduleDate) {
            this.scheduleDate = scheduleDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getCoachId() {
            return coachId;
        }

        public void setCoachId(String coachId) {
            this.coachId = coachId;
        }

        public String getCoachName() {
            return coachName;
        }

        public void setCoachName(String coachName) {
            this.coachName = coachName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getMaxStudents() {
            return maxStudents;
        }

        public void setMaxStudents(int maxStudents) {
            this.maxStudents = maxStudents;
        }

        public int getCurrentStudents() {
            return currentStudents;
        }

        public void setCurrentStudents(int currentStudents) {
            this.currentStudents = currentStudents;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}