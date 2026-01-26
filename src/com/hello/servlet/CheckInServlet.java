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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * 教练签到/签退Servlet（修复版）
 * 修复：预处理换行符为<br>，规避JSP EL解析器转义错误
 */
@WebServlet("/checkIn")
public class CheckInServlet extends HttpServlet {
    // ========== 提示文案常量（换行符改为<br>） ==========
    private static final String MSG_INVALID_CHECK_TYPE = "签到失败：无效的签到类型（仅支持上班签到/下班签退）";
    private static final String MSG_NON_WORKING_HOUR = "签到失败：当前非工作时间（6:00-22:00），仅可在工作时间内完成签到/签退";
    private static final String MSG_NO_COURSE_SCHEDULED = "签到失败：当前无课程安排，无法完成签到<br>仅可在课程开始前1小时至结束后1小时内签到";
    private static final String MSG_DUPLICATE_CHECK = "签到失败：今日已完成%s，不可重复操作";
    private static final String MSG_CHECKOUT_BEFORE_CHECKIN = "签退失败：请先完成上班签到，再进行下班签退操作";
    private static final String MSG_CHECK_SUCCESS = "%s成功！时间：%s";
    private static final String MSG_CHECK_FAILED = "%s失败：数据库操作异常，请重试";
    private static final String MSG_SYSTEM_ERROR = "签到失败：系统异常（%s），请稍后重试或联系管理员";
    private static final String MSG_QUERY_FAILED = "数据加载失败：%s";
    private static final String MSG_NOT_LOGIN = "请先登录教练账号，再进行签到操作";

    // 全局日期格式化器（ThreadLocal保证线程安全）
    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = ThreadLocal.withInitial(
            () -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                return sdf;
            }
    );
    private static final ThreadLocal<SimpleDateFormat> DATE_ONLY_FORMATTER = ThreadLocal.withInitial(
            () -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                return sdf;
            }
    );

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        System.out.println("[签到] POST请求开始处理: " + request.getRequestURI());

        // 1. 登录校验
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("role");
        Object userObj = session.getAttribute("user");

        // 检查用户角色
        if (!"coach".equals(userRole)) {
            System.out.println("[签到] 非教练用户尝试访问签到功能");
            request.setAttribute("msg", "只有教练才能使用签到功能");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/view/userinfo.jsp").forward(request, response);
            return;
        }

        Coach coach = (Coach) userObj;
        System.out.println("[签到] session user: " + coach);

        if (coach == null || coach.getId() == null || coach.getId().trim().isEmpty()) {
            System.out.println("[签到] 用户未登录，重定向到登录页");
            request.setAttribute("msg", MSG_NOT_LOGIN);
            request.setAttribute("msgType", "danger");
            loadBasicCheckInData(request, "");
            request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
            return;
        }
        String coachId = coach.getId().trim();
        String coachName = coach.getName() == null ? "" : coach.getName().trim();
        System.out.println("[签到] 当前教练ID：" + coachId + "，姓名：" + coachName);

        // 2. 签到类型校验
        String checkType = request.getParameter("checkType");
        System.out.println("[签到] checkType参数: '" + checkType + "'");

        // 调试：打印所有请求参数
        System.out.println("[签到] 所有请求参数:");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("[签到] 参数 " + paramName + " = '" + paramValue + "'");
        }

        if (checkType == null || (!"1".equals(checkType) && !"2".equals(checkType))) {
            System.out.println("[签到] 无效的签到类型: " + checkType);
            request.setAttribute("msg", MSG_INVALID_CHECK_TYPE);
            request.setAttribute("msgType", "danger");
            request.setAttribute("errorType", "invalid_type");
            loadCheckInData(request, coachId);
            request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
            return;
        }

        // 转换为统一的类型名称
        String checkTypeName = "1".equals(checkType) ? "上班签到" : "下班签退";
        System.out.println("[签到] 操作类型：" + checkTypeName);

        // 3. 初始化数据库连接
        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            // 时间合理性校验
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour < 6 || hour > 22) {
                request.setAttribute("msg", MSG_NON_WORKING_HOUR);
                request.setAttribute("msgType", "warning");
                request.setAttribute("errorType", "non_working_hour");
                loadCheckInData(request, coachId);
                request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
                return;
            }

            // 排课校验（仅一次校验，移除重复逻辑）
            System.out.println("[签到] 开始排课校验，教练ID: " + coachId);
            CourseCheckResult courseCheck = hasCourseNow(jdbcHelper, coachId);
            if (!courseCheck.hasCourse()) {
                System.out.println("[签到] 排课校验失败，教练" + coachId + "当前时段没有排课");

                // 构建详细的失败原因消息（预处理换行符）
                String detailedMsg = MSG_NO_COURSE_SCHEDULED;
                if (courseCheck.getFailureReason() != null && !courseCheck.getFailureReason().isEmpty()) {
                    detailedMsg += "<br>" + "补充提示：" + courseCheck.getFailureReason();
                }

                request.setAttribute("msg", detailedMsg);
                request.setAttribute("msgType", "warning");
                request.setAttribute("errorType", "no_course");
                loadCheckInData(request, coachId);
                request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
                return;
            }
            System.out.println("[签到] 排课校验通过，继续签到流程");

            // 核心校验：今日是否已完成该类型签到
            String today = DATE_ONLY_FORMATTER.get().format(new Date());
            String checkSql = "SELECT id, coach_id, check_type, check_time FROM check_in_record " +
                    "WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = ? LIMIT 1";

            System.out.println("[签到] 校验SQL：" + checkSql);
            System.out.println("[签到] 校验参数：coachId=" + coachId + ", today=" + today + ", type=" + checkTypeName);

            List<Map<String, Object>> checkResult = jdbcHelper.executeQueryToListNoClose(checkSql, coachId, today, checkTypeName);
            System.out.println("[签到] 校验结果数量：" + checkResult.size());

            if (!checkResult.isEmpty()) {
                // 重复操作提示（格式化文案）
                request.setAttribute("msg", String.format(MSG_DUPLICATE_CHECK, checkTypeName));
                request.setAttribute("msgType", "warning");
                request.setAttribute("errorType", "duplicate_check");
            } else {
                // 签退专属校验
                boolean canOperate = true;
                if ("下班签退".equals(checkTypeName)) {
                    String checkInSql = "SELECT id FROM check_in_record " +
                            "WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = '上班签到' LIMIT 1";
                    List<Map<String, Object>> checkInResult = jdbcHelper.executeQueryToListNoClose(checkInSql, coachId, today);
                    System.out.println("[签到] 签退校验-上班签到记录数：" + checkInResult.size());

                    if (checkInResult.isEmpty()) {
                        canOperate = false;
                        request.setAttribute("msg", MSG_CHECKOUT_BEFORE_CHECKIN);
                        request.setAttribute("msgType", "danger");
                        request.setAttribute("errorType", "checkout_before_checkin");
                    }
                }

                // 执行签到/签退插入
                if (canOperate) {
                    String currentTime = DATE_FORMATTER.get().format(new Date());
                    Timestamp checkTime = Timestamp.valueOf(currentTime);
                    String id = UUID.randomUUID().toString().replace("-", "").substring(0, 32);

                    String insertSql = "INSERT INTO check_in_record (id, coach_id, coach_name, check_type, check_time) VALUES (?, ?, ?, ?, ?)";
                    int affectedRows = jdbcHelper.executeUpdateNoClose(insertSql, id, coachId, coachName, checkTypeName, checkTime);
                    System.out.println("[签到] 插入结果：影响行数=" + affectedRows);

                    if (affectedRows > 0) {
                        request.setAttribute("msg", String.format(MSG_CHECK_SUCCESS, checkTypeName, currentTime));
                        request.setAttribute("msgType", "success");
                    } else {
                        request.setAttribute("msg", String.format(MSG_CHECK_FAILED, checkTypeName));
                        request.setAttribute("msgType", "danger");
                    }
                }
            }

            // 加载最新的签到数据
            loadCheckInData(request, coachId);

        } catch (Exception e) {
            e.printStackTrace();
            // 异常提示（展示具体异常原因，便于排查）
            String errorMsg = e.getMessage() != null ? e.getMessage().substring(0, 50) : "未知异常";
            request.setAttribute("msg", String.format(MSG_SYSTEM_ERROR, errorMsg));
            request.setAttribute("msgType", "danger");
            request.setAttribute("errorType", "system_error");
            loadBasicCheckInData(request, coachId);
        } finally {
            if (jdbcHelper != null) {
                jdbcHelper.closeDB();
                System.out.println("[签到] 数据库连接已关闭");
            }
        }

        request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("[签到] GET请求到达: " + request.getRequestURI());

        // 登录校验
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("role");
        Object userObj = session.getAttribute("user");

        // 检查用户角色
        if (!"coach".equals(userRole)) {
            System.out.println("[签到] GET请求非教练用户尝试访问签到功能");
            request.setAttribute("msg", "只有教练才能使用签到功能");
            request.setAttribute("msgType", "danger");
            request.getRequestDispatcher("/WEB-INF/view/userinfo.jsp").forward(request, response);
            return;
        }

        Coach coach = (Coach) userObj;
        System.out.println("[签到] GET请求session中的user: " + coach);

        if (coach == null || coach.getId() == null || coach.getId().trim().isEmpty()) {
            System.out.println("[签到] GET请求用户未登录，重定向到登录页");
            request.setAttribute("msg", MSG_NOT_LOGIN);
            request.setAttribute("msgType", "danger");
            loadBasicCheckInData(request, "");
            request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
            return;
        }

        String coachId = coach.getId().trim();
        System.out.println("[签到] GET请求加载数据，教练ID: " + coachId);

        // 加载签到数据并跳转页面
        loadCheckInData(request, coachId);
        request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
    }

    /**
     * 加载完整的签到数据（供JSP显示）
     */
    private void loadCheckInData(HttpServletRequest request, String coachId) {
        JdbcHelper jdbcHelper = new JdbcHelper();
        try {
            // 查询今日记录
            List<Map<String, Object>> todayRecords = getTodayCheckInRecords(jdbcHelper, coachId);
            // 统计信息
            int monthCheckinDays = getMonthCheckinDays(jdbcHelper, coachId);
            int lateCount = getLateCount(jdbcHelper, coachId);
            int earlyLeaveCount = getEarlyLeaveCount(jdbcHelper, coachId);
            boolean hasCheckedIn = hasCheckedInToday(jdbcHelper, coachId);
            boolean hasCheckedOut = hasCheckedOutToday(jdbcHelper, coachId);

            // 传递数据到JSP（仅传递必要数据，不传递调试信息）
            request.setAttribute("todayRecords", todayRecords);
            request.setAttribute("monthCheckinDays", monthCheckinDays);
            request.setAttribute("lateCount", lateCount);
            request.setAttribute("earlyLeaveCount", earlyLeaveCount);
            request.setAttribute("hasCheckedIn", hasCheckedIn);
            request.setAttribute("hasCheckedOut", hasCheckedOut);

            System.out.println("[签到] 查询记录数：" + todayRecords.size());
            System.out.println("[签到] 本月签到天数：" + monthCheckinDays + ", 迟到次数：" + lateCount + ", 早退次数：" + earlyLeaveCount);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[签到] loadCheckInData异常: " + e.getMessage());
            // 异常提示（避免覆盖原有提示）
            if (request.getAttribute("msg") == null) {
                request.setAttribute("msg", String.format(MSG_QUERY_FAILED, e.getMessage()));
                request.setAttribute("msgType", "danger");
            }
            loadBasicCheckInData(request, coachId);
        } finally {
            jdbcHelper.closeDB();
        }
    }

    /**
     * 加载基础签到数据（异常时使用）
     */
    private void loadBasicCheckInData(HttpServletRequest request, String coachId) {
        request.setAttribute("todayRecords", new ArrayList<>());
        request.setAttribute("monthCheckinDays", 0);
        request.setAttribute("lateCount", 0);
        request.setAttribute("earlyLeaveCount", 0);
        request.setAttribute("hasCheckedIn", false);
        request.setAttribute("hasCheckedOut", false);
    }

    /**
     * 查询今日签到记录
     */
    private List<Map<String, Object>> getTodayCheckInRecords(JdbcHelper jdbcHelper, String coachId) {
        try {
            String today = DATE_ONLY_FORMATTER.get().format(new Date());
            String sql = "SELECT check_type, check_time FROM check_in_record " +
                    "WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? ORDER BY check_time ASC";
            return jdbcHelper.executeQueryToListNoClose(sql, coachId, today);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 检查教练当前时段是否有排课，并返回详细的失败原因
     */
    private CourseCheckResult hasCourseNow(JdbcHelper jdbcHelper, String coachId) {
        try {
            Date now = new Date();
            String currentTime = DATE_FORMATTER.get().format(now);

            // 检查当前时间前后1小时内是否有该教练的课程
            String sql = "SELECT COUNT(*) FROM tb_course WHERE coach_id = ? AND " +
                    "course_time BETWEEN DATE_SUB(?, INTERVAL 1 HOUR) AND DATE_ADD(?, INTERVAL 1 HOUR) " +
                    "AND status != '已结束'";

            System.out.println("[签到] 排课校验SQL：" + sql);
            System.out.println("[签到] 校验参数：coachId=" + coachId + ", currentTime=" + currentTime);

            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId, currentTime, currentTime);

            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                long count = ((Number) resultList.get(0).values().iterator().next()).longValue();
                System.out.println("[签到] 当前教练ID：" + coachId + "，当前时段排课数量：" + count);
                return new CourseCheckResult(count > 0, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[签到] 排课校验异常，不允许签到");
            return new CourseCheckResult(false, "课程数据查询异常：" + e.getMessage().substring(0, 30));
        }

        // 如果没有课程，查找最近的课程安排时间
        String nextCourseInfo = getNextCourseInfo(jdbcHelper, coachId);
        return new CourseCheckResult(false, nextCourseInfo);
    }

    /**
     * 获取最近的课程安排信息
     */
    private String getNextCourseInfo(JdbcHelper jdbcHelper, String coachId) {
        try {
            Date now = new Date();
            String currentTime = DATE_FORMATTER.get().format(now);

            // 查询今天和明天的课程安排
            String sql = "SELECT course_time, course_name FROM tb_course WHERE coach_id = ? AND " +
                    "course_time >= ? AND status != '已结束' ORDER BY course_time ASC LIMIT 1";

            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId, currentTime);

            if (!resultList.isEmpty()) {
                Map<String, Object> course = resultList.get(0);
                Object courseTimeObj = course.get("course_time");
                Object courseNameObj = course.get("course_name");

                if (courseTimeObj != null) {
                    String courseTime = courseTimeObj.toString();
                    String courseName = courseNameObj != null ? courseNameObj.toString() : "未命名课程";

                    // 计算时间差
                    SimpleDateFormat sdf = DATE_FORMATTER.get();
                    Date courseDate = sdf.parse(courseTime);
                    long diffMinutes = (courseDate.getTime() - now.getTime()) / (1000 * 60);

                    if (diffMinutes <= 60) {
                        return String.format("距离下节【%s】还有%d分钟（%s）", courseName, diffMinutes, courseTime);
                    } else if (diffMinutes <= 24 * 60) {
                        long hours = diffMinutes / 60;
                        long minutes = diffMinutes % 60;
                        return String.format("下节【%s】：%s（%d小时%d分钟后）", courseName, courseTime, hours, minutes);
                    } else {
                        return String.format("下节【%s】安排在：%s", courseName, courseTime);
                    }
                }
            }

            // 如果今天没有课程，提示无近期课程
            return "您暂无近期课程安排，请联系管理员确认排课";

        } catch (Exception e) {
            e.printStackTrace();
            return "课程信息查询失败：" + e.getMessage().substring(0, 30);
        }
    }

    /**
     * 课程检查结果类
     */
    private static class CourseCheckResult {
        private final boolean hasCourse;
        private final String failureReason;

        public CourseCheckResult(boolean hasCourse, String failureReason) {
            this.hasCourse = hasCourse;
            this.failureReason = failureReason;
        }

        public boolean hasCourse() { return hasCourse; }
        public String getFailureReason() { return failureReason; }
    }

    /**
     * 获取本月签到天数
     */
    private int getMonthCheckinDays(JdbcHelper jdbcHelper, String coachId) {
        String sql = "SELECT COUNT(DISTINCT DATE(check_time)) FROM check_in_record WHERE coach_id = ? AND MONTH(check_time) = MONTH(CURRENT_DATE()) AND YEAR(check_time) = YEAR(CURRENT_DATE()) AND check_type = '上班签到'";
        try {
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId);
            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                return ((Number) resultList.get(0).values().iterator().next()).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取本月迟到次数（上班时间09:00）
     */
    private int getLateCount(JdbcHelper jdbcHelper, String coachId) {
        String sql = "SELECT COUNT(*) FROM check_in_record WHERE coach_id = ? AND MONTH(check_time) = MONTH(CURRENT_DATE()) AND YEAR(check_time) = YEAR(CURRENT_DATE()) AND check_type = '上班签到' AND TIME(check_time) > '09:00:00'";
        try {
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId);
            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                return ((Number) resultList.get(0).values().iterator().next()).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取本月早退次数（下班时间18:00）
     */
    private int getEarlyLeaveCount(JdbcHelper jdbcHelper, String coachId) {
        String sql = "SELECT COUNT(*) FROM check_in_record WHERE coach_id = ? AND MONTH(check_time) = MONTH(CURRENT_DATE()) AND YEAR(check_time) = YEAR(CURRENT_DATE()) AND check_type = '下班签退' AND TIME(check_time) < '18:00:00'";
        try {
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId);
            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                return ((Number) resultList.get(0).values().iterator().next()).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 检查今日是否已上班签到
     */
    private boolean hasCheckedInToday(JdbcHelper jdbcHelper, String coachId) {
        String today = DATE_ONLY_FORMATTER.get().format(new Date());
        String sql = "SELECT COUNT(*) FROM check_in_record WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = '上班签到'";
        try {
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId, today);
            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                return ((Number) resultList.get(0).values().iterator().next()).intValue() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检查今日是否已下班签退
     */
    private boolean hasCheckedOutToday(JdbcHelper jdbcHelper, String coachId) {
        String today = DATE_ONLY_FORMATTER.get().format(new Date());
        String sql = "SELECT COUNT(*) FROM check_in_record WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = '下班签退'";
        try {
            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId, today);
            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                return ((Number) resultList.get(0).values().iterator().next()).intValue() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}