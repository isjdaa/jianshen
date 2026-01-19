package com.hello.servlet;

import com.hello.entity.Admin;
import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.service.AdminService;
import com.hello.service.CustomerService;
import com.hello.service.CoachService;
import com.hello.utils.ApiResult;
import com.hello.utils.MD5Util; // 新增导入MD5加密工具

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    AdminService adminService = new AdminService();
    CustomerService customerService = new CustomerService();
    CoachService coachService = new CoachService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String usertype = req.getParameter("usertype");//判断角色
        String captcha = req.getParameter("captcha");
        Object sessionCaptcha= req.getSession().getAttribute("captcha");
        if(captcha == null  || !captcha.equalsIgnoreCase((String) sessionCaptcha)) {
            resp.getWriter().print(ApiResult.json(false,"验证码输入错误!"));
            return;
        }
        if (usertype.equals("admin")) {
            Admin admin = adminService.getByUsername(username);
            if (admin == null) {
                resp.getWriter().print(ApiResult.json(false, "用户不存在"));
                return;
            }
            if (admin.getPassword().equals(password)) {
                req.getSession().setAttribute("role", "admin");
                req.getSession().setAttribute("user", admin);
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }

        } else if (usertype.equals("customer")) {
            Customer customer = customerService.getById(username);
            if (customer == null) {
                resp.getWriter().print(ApiResult.json(false, "用户不存在"));
                return;
            }
            // 客户密码做MD5加密后再对比
            if (customer.getPassword().equals(MD5Util.md5Encode(password))) {
                req.getSession().setAttribute("user", customer);
                req.getSession().setAttribute("role", "customer");
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }
        } else if (usertype.equals("coach")) {
            Coach coach = coachService.getById(username);
            if (coach == null) {
                resp.getWriter().print(ApiResult.json(false, "用户不存在"));
                return;
            }
            // 教练密码做MD5加密后再对比
            if (coach.getPassword().equals(MD5Util.md5Encode(password))) {
                req.getSession().setAttribute("user", coach);
                req.getSession().setAttribute("role", "coach");
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }
        } else {
            resp.getWriter().print(ApiResult.json(false, "未知用户类型"));
            return;
        }
    }
}