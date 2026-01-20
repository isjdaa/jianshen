package com.hello.servlet;

import com.hello.dao.CustomerDAO;
import com.hello.dao.CoachDAO;
import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.utils.ApiResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/password"})
public class PasswordServlet extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();
    private CoachDAO coachDAO = new CoachDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 获取当前登录用户
        Object userObj = req.getSession().getAttribute("user");

        if (userObj == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 跳转到修改密码页面
        req.getRequestDispatcher("/WEB-INF/view/password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 获取当前登录用户
        Object userObj = req.getSession().getAttribute("user");
        String role = (String) req.getSession().getAttribute("role");
        ApiResult apiResult = new ApiResult();

        if (userObj == null) {
            apiResult.error("请先登录");
            out.print(apiResult.toJson());
            out.close();
            return;
        }

        try {
            // 获取请求参数
            String oldPassword = req.getParameter("oldPassword") != null ? req.getParameter("oldPassword").trim() : "";
            String newPassword = req.getParameter("newPassword") != null ? req.getParameter("newPassword").trim() : "";

            if (oldPassword.equals("")) {
                apiResult.error("请输入原密码");
                out.print(apiResult.toJson());
                out.close();
                return;
            }

            if (newPassword.equals("")) {
                apiResult.error("请输入新密码");
                out.print(apiResult.toJson());
                out.close();
                return;
            }

            if (newPassword.length() < 6) {
                apiResult.error("新密码长度不能少于6位");
                out.print(apiResult.toJson());
                out.close();
                return;
            }

            boolean success = false;

            if ("customer".equals(role)) {
                // 客户修改密码
                Customer customer = (Customer) userObj;
                // 验证原密码
                if (!customer.getPassword().equals(oldPassword)) {
                    apiResult.error("原密码错误");
                } else {
                    // 更新密码
                    customer.setPassword(newPassword);
                    customerDAO.update(customer);
                    // 更新会话中的用户信息
                    req.getSession().setAttribute("user", customer);
                    apiResult.success("密码修改成功");
                    success = true;
                }
            } else if ("coach".equals(role)) {
                // 教练修改密码
                Coach coach = (Coach) userObj;
                // 验证原密码
                if (!coach.getPassword().equals(oldPassword)) {
                    apiResult.error("原密码错误");
                } else {
                    // 更新密码
                    coach.setPassword(newPassword);
                    coachDAO.update(coach);
                    // 更新会话中的用户信息
                    req.getSession().setAttribute("user", coach);
                    apiResult.success("密码修改成功");
                    success = true;
                }
            } else {
                apiResult.error("不支持的用户类型");
            }

        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("修改失败：" + e.getMessage());
        }

        out.print(apiResult.toJson());
        out.flush();
        out.close();
    }
}