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
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 教练签到/签退Servlet（修复版）
 * 修复：参数匹配、连接管理、排课校验、数据回显等问题
 */
@WebServlet("/checkIn")
public class CheckInServlet extends HttpServlet {
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
        Coach coach = (Coach) session.getAttribute("user");
        System.out.println("[签到] session user: " + coach);

        if (coach == null || coach.getId() == null || coach.getId().trim().isEmpty()) {
            System.out.println("[签到] 用户未登录，重定向到登录页");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        String coachId = coach.getId().trim();
        String coachName = coach.getName() == null ? "" : coach.getName().trim();
        System.out.println("[签到] 当前教练ID：" + coachId + "，姓名：" + coachName);

        // 2. 签到类型校验
        String checkType = request.getParameter("checkType");
        System.out.println("[签到] checkType参数: " + checkType);

        if (checkType == null || (!"1".equals(checkType) && !"2".equals(checkType))) {
            System.out.println("[签到] 无效的签到类型: " + checkType);
            request.setAttribute("msg", "无效的签到类型！");
            request.setAttribute("msgType", "danger"); // 错误消息用红色
            loadCheckInData(request, coachId); // 加载基础数据
            request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
            return;
        }

        // 转换为统一的类型名称（避免硬编码错误）
        String checkTypeName = "1".equals(checkType) ? "上班签到" : "下班签退";
        System.out.println("[签到] 操作类型：" + checkTypeName);

        // 3. 初始化数据库连接（全程使用同一个连接，避免多次创建/关闭）
        JdbcHelper jdbcHelper = new JdbcHelper();
        List<Map<String, Object>> todayRecords = new ArrayList<>();

        try {
            // 1. 时间合理性校验（可选）：检查是否在正常工作时间内
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour < 6 || hour > 22) {
                request.setAttribute("msg", "当前时间段为非工作时间（6:00-22:00），签到功能暂时不可用。如有特殊情况请联系管理员。");
                request.setAttribute("msgType", "warning");
                loadCheckInData(request, coachId);
                request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
                return;
            }

            // 2. 严格排课校验：只有在有课期间才能签到
            System.out.println("[签到] 开始排课校验，教练ID: " + coachId);
            if (!hasCourseNow(jdbcHelper, coachId)) {
                System.out.println("[签到] 排课校验失败，教练" + coachId + "当前时段没有排课");
                request.setAttribute("msg", "当前时段没有排课，无法签到！请在课程开始前1小时到课程结束后1小时内进行签到。如需测试，请联系管理员添加测试课程。");
                request.setAttribute("msgType", "warning"); // 警告消息用黄色
                System.out.println("[签到] 设置错误消息: " + request.getAttribute("msg"));
                loadCheckInData(request, coachId); // 加载基础数据
                System.out.println("[签到] loadCheckInData执行完毕，最终msg: " + request.getAttribute("msg"));
                request.getRequestDispatcher("/checkIn.jsp").forward(request, response);
                return; // 直接返回，不继续执行签到逻辑
            }
            System.out.println("[签到] 排课校验通过，继续签到流程");

            // 4. 核心校验：今日是否已完成该类型签到
            String today = DATE_ONLY_FORMATTER.get().format(new Date());
            String checkSql = "SELECT id, coach_id, check_type, check_time FROM check_in_record " +
                    "WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = ? LIMIT 1";

            System.out.println("[签到] 校验SQL：" + checkSql);
            System.out.println("[签到] 校验参数：coachId=" + coachId + ", today=" + today + ", type=" + checkTypeName);

            List<Map<String, Object>> checkResult = jdbcHelper.executeQueryToListNoClose(checkSql, coachId, today, checkTypeName);
            System.out.println("[签到] 校验结果数量：" + checkResult.size());

            if (!checkResult.isEmpty()) {
                // 重复操作，禁止
                request.setAttribute("msg", "您今日已经完成" + checkTypeName + "，不能重复操作！如需修改请联系管理员。");
                request.setAttribute("msgType", "warning"); // 重复操作用黄色警告
            } else {
                // 5. 签退专属校验：必须先上班签到
                boolean canOperate = true;
                if ("下班签退".equals(checkTypeName)) {
                    String checkInSql = "SELECT id FROM check_in_record " +
                            "WHERE coach_id = ? AND DATE_FORMAT(check_time, '%Y-%m-%d') = ? AND check_type = '上班签到' LIMIT 1";
                    List<Map<String, Object>> checkInResult = jdbcHelper.executeQueryToListNoClose(checkInSql, coachId, today);
                    System.out.println("[签到] 签退校验-上班签到记录数：" + checkInResult.size());

                    if (checkInResult.isEmpty()) {
                        canOperate = false;
                        request.setAttribute("msg", "请先完成上班签到，才能进行下班签退！请确保今日已进行过上班签到。");
                        request.setAttribute("msgType", "danger"); // 顺序错误用红色
                    }
                }

                // 6. 执行签到/签退插入 - 只有在有课期间才能签到
                if (canOperate && hasCourseNow(jdbcHelper, coachId)) {
                    String currentTime = DATE_FORMATTER.get().format(new Date());
                    Timestamp checkTime = Timestamp.valueOf(currentTime);
                    // 生成唯一ID
                    String id = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
                    // 更新SQL，添加ID字段
                    String insertSql = "INSERT INTO check_in_record (id, coach_id, coach_name, check_type, check_time) VALUES (?, ?, ?, ?, ?)";
                    int affectedRows = jdbcHelper.executeUpdateNoClose(insertSql, id, coachId, coachName, checkTypeName, checkTime);
                    System.out.println("[签到] 插入结果：影响行数=" + affectedRows);

                    if (affectedRows > 0) {
                        request.setAttribute("msg", checkTypeName + "成功！时间：" + currentTime);
                        request.setAttribute("msgType", "success"); // 成功消息用绿色
                    } else {
                        request.setAttribute("msg", checkTypeName + "失败，请重试！");
                        request.setAttribute("msgType", "danger"); // 失败消息用红色
                    }
                } else if (canOperate && !hasCourseNow(jdbcHelper, coachId)) {
                    // 如果没有排课，不执行签到操作，显示提示信息
                    request.setAttribute("msg", "当前时段没有排课，无法签到！请在课程开始前1小时到课程结束后1小时内进行签到。");
                    request.setAttribute("msgType", "warning"); // 警告消息用黄色
                }
            }

            // 7. 加载最新的签到数据（统一方法）
            loadCheckInData(request, coachId);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "系统异常，请稍后重试或联系管理员！");
            request.setAttribute("msgType", "danger"); // 异常消息用红色
            // 异常时仍加载基础数据
            loadBasicCheckInData(request, coachId);
        } finally {
            if (jdbcHelper != null) {
                jdbcHelper.closeDB(); // 确保连接关闭
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
        Coach coach = (Coach) session.getAttribute("user");
        System.out.println("[签到] GET请求session中的user: " + coach);

        if (coach == null || coach.getId() == null || coach.getId().trim().isEmpty()) {
            System.out.println("[签到] GET请求用户未登录，重定向到登录页");
            response.sendRedirect(request.getContextPath() + "/login.jsp");
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

            // 传递数据到JSP
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
            // 只在msg属性为空时才设置新的错误信息，避免覆盖之前的签到提示
            if (request.getAttribute("msg") == null) {
                request.setAttribute("msg", "查询记录失败：" + e.getMessage());
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
     * 检查教练当前时段是否有排课
     */
    private boolean hasCourseNow(JdbcHelper jdbcHelper, String coachId) {
        try {
            Date now = new Date();
            String currentTime = DATE_FORMATTER.get().format(now);

            // 检查当前时间前后1小时内是否有该教练的课程
            // 课程时间在当前时间前1小时到后1小时范围内，且状态不是已结束
            String sql = "SELECT COUNT(*) FROM tb_course WHERE coach_id = ? AND " +
                        "course_time BETWEEN DATE_SUB(?, INTERVAL 1 HOUR) AND DATE_ADD(?, INTERVAL 1 HOUR) " +
                        "AND status != '已结束'";

            System.out.println("[签到] 排课校验SQL：" + sql);
            System.out.println("[签到] 校验参数：coachId=" + coachId + ", currentTime=" + currentTime);

            List<Map<String, Object>> resultList = jdbcHelper.executeQueryToListNoClose(sql, coachId, currentTime, currentTime);

            if (!resultList.isEmpty() && resultList.get(0).values().iterator().next() != null) {
                long count = ((Number) resultList.get(0).values().iterator().next()).longValue();
                System.out.println("[签到] 当前教练ID：" + coachId + "，当前时段排课数量：" + count);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[签到] 排课校验异常，允许签到");
        }
        return false; // 没有排课或异常时不允许签到
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