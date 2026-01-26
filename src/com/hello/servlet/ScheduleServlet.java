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
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 排课查询Servlet
 * 最终修复版：
 * 1. 解决course_time多类型兼容（String/Date/Timestamp）
 * 2. 移除所有排课LIMIT限制
 * 3. 完善空值安全处理
 * 4. 正确传递today变量到前端
 * 5. 修复location字段逻辑
 * 6. 核心修复：管理员看不到排课记录的问题（过滤空course_time + 增强异常处理）
 */
@WebServlet("/schedule")
public class ScheduleServlet extends HttpServlet {
    // 日期格式化器（只显示日期）
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    // 时间格式化器（显示完整时间）
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 验证登录
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("role");
        Object userObj = session.getAttribute("user");

        // 检查用户是否登录
        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 根据用户角色处理用户对象
        Coach coach = null;
        if ("coach".equals(userRole)) {
            coach = (Coach) userObj;
        }

        String action = request.getParameter("action");
        if ("add".equals(action) || "update".equals(action)) {
            // 添加或更新排课（管理员权限）
            if (!"admin".equals(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限执行此操作");
                return;
            }
            handleSave(request, response, "add".equals(action));
        } else if ("delete".equals(action)) {
            // 删除排课（管理员权限）
            if (!"admin".equals(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限执行此操作");
                return;
            }
            handleDelete(request, response);
        } else {
            // 查询操作
            handleQuery(request, response, userRole, coach);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        // 验证登录
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("role");
        Object userObj = session.getAttribute("user");

        // 检查用户是否登录
        if (userObj == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // 根据用户角色处理用户对象
        Coach coach = null;
        if ("coach".equals(userRole)) {
            coach = (Coach) userObj;
        }

        String action = request.getParameter("action");
        if ("add".equals(action)) {
            // 显示添加表单（管理员权限）
            if (!"admin".equals(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问此功能");
                return;
            }
            showAddForm(request, response);
        } else if ("edit".equals(action)) {
            // 显示编辑表单（管理员权限）
            if (!"admin".equals(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "没有权限访问此功能");
                return;
            }
            showEditForm(request, response);
        } else {
            // 显示列表页面
            showList(request, response, userRole, coach);
        }
    }

    // 内部类：排课记录
    private static class ScheduleRecord {
        private String id;
        private String scheduleDate; // 只保存日期 yyyy-MM-dd
        private String startTime;    // 保存时间 HH:mm
        private String endTime;      // 保存时间 HH:mm
        private String courseName;
        private String coachId;
        private String coachName;
        private String location;
        private int duration;        // 课程时长(分钟)
        private int maxStudents;
        private int currentStudents;
        private String status;
        private String description;  // 课程描述
        private String scheduleId;   // 排课ID
        private boolean isGenerated; // 是否自动生成

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getScheduleDate() { return scheduleDate; }
        public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }
        public String getStartTime() { return startTime; }
        public void setStartTime(String startTime) { this.startTime = startTime; }
        public String getEndTime() { return endTime; }
        public void setEndTime(String endTime) { this.endTime = endTime; }
        public String getCourseName() { return courseName; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public String getCoachId() { return coachId; }
        public void setCoachId(String coachId) { this.coachId = coachId; }
        public String getCoachName() { return coachName; }
        public void setCoachName(String coachName) { this.coachName = coachName; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        public int getMaxStudents() { return maxStudents; }
        public void setMaxStudents(int maxStudents) { this.maxStudents = maxStudents; }
        public int getCurrentStudents() { return currentStudents; }
        public void setCurrentStudents(int currentStudents) { this.currentStudents = currentStudents; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getScheduleId() { return scheduleId; }
        public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }
        public boolean isGenerated() { return isGenerated; }
        public void setGenerated(boolean isGenerated) { this.isGenerated = isGenerated; }
    }

    /**
     * 格式化时间戳为日期字符串 (yyyy-MM-dd)
     */
    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATE_FORMAT.format(timestamp);
    }

    /**
     * 格式化时间戳为时间字符串 (HH:mm)
     */
    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(timestamp);
    }

    /**
     * 计算结束时间并格式化为 HH:mm
     */
    private String calculateEndTime(Timestamp courseTime, int duration) {
        if (courseTime == null || duration <= 0) return "";

        Calendar cal = Calendar.getInstance();
        cal.setTime(courseTime);
        cal.add(Calendar.MINUTE, duration);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    /**
     * 兼容转换course_time为Timestamp（核心修复：解决列表为空问题）
     */
    private Timestamp convertCourseTime(Object courseTimeObj) {
        if (courseTimeObj == null) return null;
        Timestamp courseTime = null;
        try {
            if (courseTimeObj instanceof String) {
                // 处理字符串类型的时间（如"2026-01-26 10:00:00"）
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse((String) courseTimeObj);
                courseTime = new Timestamp(date.getTime());
            } else if (courseTimeObj instanceof Date) {
                // 处理Date/Datetime类型
                courseTime = new Timestamp(((Date) courseTimeObj).getTime());
            } else if (courseTimeObj instanceof Timestamp) {
                // 处理Timestamp类型
                courseTime = (Timestamp) courseTimeObj;
            }
        } catch (ParseException e) {
            System.out.println("转换course_time字符串失败：" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("转换course_time失败：" + e.getMessage());
            e.printStackTrace();
        }
        return courseTime;
    }

    /**
     * 显示列表页面 - 修复管理员看不到数据的问题
     */
    private void showList(HttpServletRequest request, HttpServletResponse response, String userRole, Coach coach)
            throws ServletException, IOException {

        request.setAttribute("userRole", userRole);
        request.setAttribute("coach", coach);

        // 关键：设置今日日期，用于前端默认显示
        request.setAttribute("today", DATE_FORMAT.format(new Date()));

        System.out.println("=== 显示排课列表页面，用户角色: " + userRole + " ===");

        // 如果是管理员，获取统计信息
        if ("admin".equals(userRole)) {
            loadAdminStatistics(request);
        }

        // 默认加载数据
        JdbcHelper jdbcHelper = new JdbcHelper();
        List<ScheduleRecord> scheduleList = new ArrayList<>();

        try {
            String sql;
            Object[] params;
            String message;

            if ("admin".equals(userRole)) {
                // 修复：管理员默认查看所有排课，但添加WHERE条件确保course_time不为空
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.course_time IS NOT NULL ORDER BY c.course_time DESC";
                params = null;
                message = "管理员默认查看所有排课";
                System.out.println("管理员查看所有排课（过滤空时间）");
            } else {
                // 教练和客户默认查看今日排课
                String today = DATE_FORMAT.format(new Date());
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE DATE(c.course_time) = ? ORDER BY c.course_time";
                params = new Object[]{today};
                message = "默认加载今日排课";
                System.out.println("非管理员查看今日排课，日期: " + today);
            }

            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, params);
            // 调试：打印原始数据数量（确认数据库是否返回数据）
            System.out.println("排课列表查询到的原始数据数量：" + resultList.size());
            System.out.println("SQL查询：" + sql);

            // 转换为ScheduleRecord列表
            int successCount = 0;
            int failCount = 0;
            for (Map<String, Object> row : resultList) {
                try {
                    ScheduleRecord record = new ScheduleRecord();
                    record.setId(getStringValue(row, "id"));

                    // 核心修复：兼容转换course_time，添加空值判断
                    Object courseTimeObj = row.get("course_time");
                    if (courseTimeObj == null) {
                        failCount++;
                        System.out.println("跳过空course_time的记录，ID: " + getStringValue(row, "id"));
                        continue;
                    }

                    Timestamp courseTime = convertCourseTime(courseTimeObj);
                    if (courseTime != null) {
                        record.setScheduleDate(formatDate(courseTime));
                        record.setStartTime(formatTime(courseTime));

                        // 计算结束时间
                        int duration = getIntValue(row, "duration");
                        record.setEndTime(calculateEndTime(courseTime, duration));
                    } else {
                        record.setScheduleDate("");
                        record.setStartTime("");
                        record.setEndTime("");
                    }

                    record.setCourseName(getStringValue(row, "course_name"));
                    record.setCoachId(getStringValue(row, "coach_id"));
                    record.setCoachName(getStringValue(row, "coach_name"));

                    // 使用数据库中的location值，为空则默认"健身房"
                    String location = getStringValue(row, "location");
                    record.setLocation(location.isEmpty() ? "健身房" : location);

                    record.setDuration(getIntValue(row, "duration"));
                    record.setMaxStudents(getIntValue(row, "max_students"));
                    record.setCurrentStudents(getIntValue(row, "current_students"));
                    record.setStatus(getStringValue(row, "status"));
                    record.setDescription(getStringValue(row, "description"));
                    record.setScheduleId(getStringValue(row, "schedule_id"));
                    record.setGenerated(getIntValue(row, "is_generated") == 1);

                    scheduleList.add(record);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    System.out.println("处理单条排课记录出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("msg", message + "，共找到 " + scheduleList.size() + " 条记录（成功转换: " + successCount + "，失败: " + failCount + "）");
            System.out.println("=== 排课数据加载完成，最终列表数量：" + scheduleList.size() + " ===");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "加载排课数据失败：" + e.getMessage());
            System.out.println("=== 排课数据加载异常: " + e.getMessage() + " ===");
        } finally {
            if (jdbcHelper != null) {
                jdbcHelper.closeDB();
            }
        }

        request.getRequestDispatcher("/schedule.jsp").forward(request, response);
    }

    /**
     * 处理查询操作 - 同步修复管理员查询逻辑
     */
    private void handleQuery(HttpServletRequest request, HttpServletResponse response, String userRole, Coach coach)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // 获取查询类型
        String queryType = request.getParameter("queryType");
        List<ScheduleRecord> scheduleList = new ArrayList<>();

        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            System.out.println("=== 处理排课查询，类型: " + queryType + "，角色: " + userRole + " ===");

            String sql = "";
            Object[] params = null;

            // 根据查询类型拼接SQL
            if ("1".equals(queryType)) {
                // 今日排课
                String today = DATE_FORMAT.format(new Date());
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE DATE(c.course_time) = ? ORDER BY c.course_time";
                params = new Object[]{today};
                System.out.println("今日排课查询，日期: " + today);
            } else if ("2".equals(queryType)) {
                // 我的排课（仅教练）
                if (coach == null) {
                    request.setAttribute("msg", "管理员无法查询个人排课！");
                    request.getRequestDispatcher("/schedule.jsp").forward(request, response);
                    return;
                }
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.coach_id = ? ORDER BY c.course_time";
                params = new Object[]{coach.getId()};
                System.out.println("我的排课查询，教练ID: " + coach.getId());
            } else if ("3".equals(queryType)) {
                // 按日期查询
                String queryDate = request.getParameter("queryDate");
                if (queryDate == null || queryDate.isEmpty()) {
                    request.setAttribute("msg", "请输入查询日期！");
                    request.getRequestDispatcher("/schedule.jsp").forward(request, response);
                    return;
                }
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE DATE(c.course_time) = ? ORDER BY c.course_time";
                params = new Object[]{queryDate};
                System.out.println("按日期查询，日期: " + queryDate);
            } else if ("4".equals(queryType)) {
                // 所有排课（管理员）- 修复：添加非空过滤
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.course_time IS NOT NULL ORDER BY c.course_time DESC";
                params = null;
                System.out.println("所有排课查询（管理员）- 过滤空时间");
            } else {
                // 默认今日排课
                String today = DATE_FORMAT.format(new Date());
                sql = "SELECT c.*, co.name as coach_name FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE DATE(c.course_time) = ? ORDER BY c.course_time";
                params = new Object[]{today};
                System.out.println("默认今日排课查询，日期: " + today);
            }

            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, params);
            System.out.println("查询到原始数据数量：" + resultList.size());
            System.out.println("SQL查询：" + sql);

            // 处理结果集（添加失败计数）
            int successCount = 0;
            int failCount = 0;
            for (Map<String, Object> row : resultList) {
                try {
                    ScheduleRecord record = new ScheduleRecord();
                    record.setId(getStringValue(row, "id"));

                    // 核心修复：先判断是否为空
                    Object courseTimeObj = row.get("course_time");
                    if (courseTimeObj == null) {
                        failCount++;
                        continue;
                    }

                    // 兼容转换course_time
                    Timestamp courseTime = convertCourseTime(courseTimeObj);
                    if (courseTime != null) {
                        record.setScheduleDate(formatDate(courseTime));
                        record.setStartTime(formatTime(courseTime));

                        // 计算结束时间
                        int duration = getIntValue(row, "duration");
                        record.setEndTime(calculateEndTime(courseTime, duration));
                    } else {
                        record.setScheduleDate("");
                        record.setStartTime("");
                        record.setEndTime("");
                    }

                    record.setCourseName(getStringValue(row, "course_name"));
                    record.setCoachId(getStringValue(row, "coach_id"));
                    record.setCoachName(getStringValue(row, "coach_name"));

                    // 使用数据库中的location值
                    String location = getStringValue(row, "location");
                    record.setLocation(location.isEmpty() ? "健身房" : location);

                    record.setDuration(getIntValue(row, "duration"));
                    record.setMaxStudents(getIntValue(row, "max_students"));
                    record.setCurrentStudents(getIntValue(row, "current_students"));
                    record.setStatus(getStringValue(row, "status"));
                    record.setDescription(getStringValue(row, "description"));
                    record.setScheduleId(getStringValue(row, "schedule_id"));
                    record.setGenerated(getIntValue(row, "is_generated") == 1);

                    scheduleList.add(record);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    System.out.println("处理查询结果记录出错: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // 存入查询结果
            request.setAttribute("scheduleList", scheduleList);
            request.setAttribute("msg", "查询成功，共找到 " + scheduleList.size() + " 条记录（成功转换: " + successCount + "，失败: " + failCount + "）");

            // 管理员重新加载统计
            if ("admin".equals(userRole)) {
                loadAdminStatistics(request);
            }

            System.out.println("=== 排课查询完成，返回列表数量：" + scheduleList.size() + " ===");

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "查询失败：" + e.getMessage());
            System.out.println("=== 排课查询异常: " + e.getMessage() + " ===");
        } finally {
            if (jdbcHelper != null) {
                jdbcHelper.closeDB();
            }
        }

        request.getRequestDispatcher("/schedule.jsp").forward(request, response);
    }

    // ========== 工具方法 ==========

    /**
     * 安全获取字符串值，处理null
     */
    private String getStringValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? "" : value.toString().trim();
    }

    /**
     * 安全获取整数值，处理null/类型转换
     */
    private int getIntValue(Map<String, Object> row, String key) {
        Object value = row.get(key);
        if (value == null) return 0;

        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return ((Long) value).intValue();
        } else {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }

    /**
     * 显示添加表单
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Coach> coaches = getAllCoaches();
        request.setAttribute("coaches", coaches);
        request.setAttribute("isEdit", false);
        request.getRequestDispatcher("/WEB-INF/view/schedule-form.jsp").forward(request, response);
    }

    /**
     * 显示编辑表单
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "课程ID不能为空");
            return;
        }

        ScheduleRecord schedule = getScheduleById(id);
        if (schedule == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "课程不存在");
            return;
        }

        List<Coach> coaches = getAllCoaches();
        request.setAttribute("schedule", schedule);
        request.setAttribute("coaches", coaches);
        request.setAttribute("isEdit", true);
        request.getRequestDispatcher("/WEB-INF/view/schedule-form.jsp").forward(request, response);
    }

    /**
     * 处理保存操作（添加/更新）
     */
    private void handleSave(HttpServletRequest request, HttpServletResponse response, boolean isAdd)
            throws IOException {
        response.setContentType("application/json; charset=utf-8");

        try {
            String id = request.getParameter("id");
            String coachId = request.getParameter("coachId");
            String courseName = request.getParameter("courseName");
            String courseTime = request.getParameter("courseTime");
            String duration = request.getParameter("duration");
            String maxStudents = request.getParameter("maxStudents");
            String description = request.getParameter("description");

            // 调试信息
            System.out.println("=== 保存排课，操作: " + (isAdd ? "添加" : "更新") + " ===");
            System.out.println("教练ID: " + coachId + "，课程名: " + courseName + "，时间: " + courseTime);

            // 参数校验
            if (coachId == null || coachId.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"请选择教练\"}");
                return;
            }
            if (!isCoachExists(coachId)) {
                response.getWriter().write("{\"success\":false,\"message\":\"选择的教练不存在\"}");
                return;
            }
            if (courseName == null || courseName.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"请输入课程名称\"}");
                return;
            }
            if (courseTime == null || courseTime.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"请选择课程时间\"}");
                return;
            }
            if (duration == null || duration.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"请选择课程时长\"}");
                return;
            }
            if (maxStudents == null || maxStudents.trim().isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"请选择最大报名人数\"}");
                return;
            }

            // 数值校验
            int durationInt, maxStudentsInt;
            try {
                durationInt = Integer.parseInt(duration);
                maxStudentsInt = Integer.parseInt(maxStudents);
            } catch (NumberFormatException e) {
                response.getWriter().write("{\"success\":false,\"message\":\"时长和人数必须是数字\"}");
                return;
            }
            if (durationInt <= 0 || maxStudentsInt <= 0) {
                response.getWriter().write("{\"success\":false,\"message\":\"时长和人数必须大于0\"}");
                return;
            }

            JdbcHelper jdbcHelper = new JdbcHelper();
            try {
                String sql;
                Object[] params;

                // 处理时间格式：datetime-local -> yyyy-MM-dd HH:mm:ss
                String formattedCourseTime = courseTime.replace("T", " ") + ":00";

                if (isAdd) {
                    // 添加操作
                    String newId = "COURSE" + System.currentTimeMillis() + (int)(Math.random() * 1000);
                    sql = "INSERT INTO tb_course (id, course_name, coach_id, course_time, duration, max_students, description, status, current_students) VALUES (?, ?, ?, ?, ?, ?, ?, '未开始', 0)";
                    params = new Object[]{
                            newId, courseName, coachId, formattedCourseTime,
                            durationInt, maxStudentsInt, description != null ? description : ""
                    };
                } else {
                    // 更新操作
                    sql = "UPDATE tb_course SET course_name = ?, coach_id = ?, course_time = ?, duration = ?, max_students = ?, description = ? WHERE id = ?";
                    params = new Object[]{
                            courseName, coachId, formattedCourseTime,
                            durationInt, maxStudentsInt, description != null ? description : "", id
                    };
                }

                int result = jdbcHelper.executeUpdateNoClose(sql, params);
                if (result > 0) {
                    response.getWriter().write("{\"success\":true,\"message\":\"" + (isAdd ? "添加" : "更新") + "成功\"}");
                } else {
                    response.getWriter().write("{\"success\":false,\"message\":\"操作失败，数据库未更新\"}");
                }
            } finally {
                jdbcHelper.closeDB();
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"操作失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 处理删除操作
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");

        String id = request.getParameter("id");
        if (id == null || id.trim().isEmpty()) {
            response.getWriter().write("{\"success\":false,\"message\":\"课程ID不能为空\"}");
            return;
        }

        try {
            JdbcHelper jdbcHelper = new JdbcHelper();
            String sql = "DELETE FROM tb_course WHERE id = ?";
            int result = jdbcHelper.executeUpdate(sql, id);
            jdbcHelper.closeDB();

            if (result > 0) {
                response.getWriter().write("{\"success\":true,\"message\":\"删除成功\"}");
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"删除失败，课程不存在\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"删除失败：" + e.getMessage() + "\"}");
        }
    }

    /**
     * 获取所有教练
     */
    private List<Coach> getAllCoaches() {
        List<Coach> coaches = new ArrayList<>();
        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            String sql = "SELECT id, name FROM tb_coach ORDER BY name";
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql);
            for (Map<String, Object> row : resultList) {
                Coach coach = new Coach();
                coach.setId(getStringValue(row, "id"));
                coach.setName(getStringValue(row, "name"));
                coaches.add(coach);
            }
            System.out.println("获取教练列表，数量：" + coaches.size());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbcHelper.closeDB();
        }

        return coaches;
    }

    /**
     * 根据ID获取课程信息
     */
    private ScheduleRecord getScheduleById(String id) {
        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            String sql = "SELECT c.*, co.name as coach_name, c.location FROM tb_course c LEFT JOIN tb_coach co ON c.coach_id = co.id WHERE c.id = ?";
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, id);

            if (!resultList.isEmpty()) {
                Map<String, Object> row = resultList.get(0);
                ScheduleRecord record = new ScheduleRecord();

                record.setId(getStringValue(row, "id"));
                // 兼容转换course_time
                Timestamp courseTime = convertCourseTime(row.get("course_time"));
                if (courseTime != null) {
                    record.setScheduleDate(formatDate(courseTime));
                    record.setStartTime(formatTime(courseTime));
                    int duration = getIntValue(row, "duration");
                    record.setEndTime(calculateEndTime(courseTime, duration));
                }

                record.setCourseName(getStringValue(row, "course_name"));
                record.setCoachId(getStringValue(row, "coach_id"));
                record.setCoachName(getStringValue(row, "coach_name"));
                record.setLocation(getStringValue(row, "location").isEmpty() ? "健身房" : getStringValue(row, "location"));
                record.setMaxStudents(getIntValue(row, "max_students"));
                record.setCurrentStudents(getIntValue(row, "current_students"));
                record.setStatus(getStringValue(row, "status"));

                return record;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbcHelper.closeDB();
        }

        return null;
    }

    /**
     * 加载管理员统计信息
     */
    private void loadAdminStatistics(HttpServletRequest request) {
        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            // 今日课程数
            String today = DATE_FORMAT.format(new Date());
            List<Map<String, Object>> todayResult = jdbcHelper.executeQueryToListNoClose(
                    "SELECT COUNT(*) as count FROM tb_course WHERE DATE(course_time) = ?", today);
            if (!todayResult.isEmpty()) {
                request.setAttribute("todayCount", getIntValue(todayResult.get(0), "count"));
            }

            // 本周课程数
            Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            String weekStart = DATE_FORMAT.format(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 6);
            String weekEnd = DATE_FORMAT.format(cal.getTime());

            List<Map<String, Object>> weekResult = jdbcHelper.executeQueryToListNoClose(
                    "SELECT COUNT(*) as count FROM tb_course WHERE DATE(course_time) BETWEEN ? AND ?", weekStart, weekEnd);
            if (!weekResult.isEmpty()) {
                request.setAttribute("weekCount", getIntValue(weekResult.get(0), "count"));
            }

            // 总课程数
            List<Map<String, Object>> totalResult = jdbcHelper.executeQueryToListNoClose("SELECT COUNT(*) as count FROM tb_course");
            if (!totalResult.isEmpty()) {
                request.setAttribute("totalCount", getIntValue(totalResult.get(0), "count"));
            }

            // 活跃教练数
            List<Map<String, Object>> coachResult = jdbcHelper.executeQueryToListNoClose("SELECT COUNT(DISTINCT coach_id) as count FROM tb_course");
            if (!coachResult.isEmpty()) {
                request.setAttribute("coachCount", getIntValue(coachResult.get(0), "count"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbcHelper.closeDB();
        }
    }

    /**
     * 检查教练是否存在
     */
    private boolean isCoachExists(String coachId) {
        JdbcHelper jdbcHelper = new JdbcHelper();
        try {
            String sql = "SELECT COUNT(*) as count FROM tb_coach WHERE id = ?";
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId);
            return !resultList.isEmpty() && getIntValue(resultList.get(0), "count") > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jdbcHelper.closeDB();
        }
        return false;
    }
}