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
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 上下班签到Servlet
 */
@WebServlet("/checkIn")
public class CheckInServlet extends HttpServlet {
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

        // 获取签到类型
        String checkType = request.getParameter("checkType");
        if (checkType == null || (!checkType.equals("1") && !checkType.equals("2"))) {
            request.setAttribute("msg", "请选择正确的签到类型！");
            request.getRequestDispatcher("checkIn.jsp").forward(request, response);
            return;
        }

        // 转换签到类型
        String checkTypeText = checkType.equals("1") ? "上班签到" : "下班签退";
        // 获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkTime = sdf.format(new Date());

        JdbcHelper jdbcHelper = new JdbcHelper();

        try {
            // 编写SQL
            String sql = "INSERT INTO check_in_record (coach_id, coach_name, check_type, check_time) VALUES (?, ?, ?, ?)";
            // 执行插入
            int rows = jdbcHelper.executeUpdate(sql, coach.getId(), coach.getName(), checkTypeText, checkTime);
            if (rows > 0) {
                request.setAttribute("msg", checkTypeText + "成功！时间：" + checkTime);
            } else {
                request.setAttribute("msg", checkTypeText + "失败，请重试！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "签到失败，数据库异常！");
        } finally {
            // 关闭资源
            jdbcHelper.closeDB();
        }

        request.getRequestDispatcher("checkIn.jsp").forward(request, response);
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
        
        // 直接跳转到签到页面
        request.getRequestDispatcher("checkIn.jsp").forward(request, response);
    }
}