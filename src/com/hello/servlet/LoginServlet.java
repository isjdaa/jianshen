package com.hello.servlet;

import com.hello.entity.Admin;
import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.service.AdminService;
import com.hello.service.CustomerService;
import com.hello.service.CoachService;
import com.hello.utils.ApiResult;

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
        // 打印所有登录请求参数
        System.out.println("=== Login Request Received ===");
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Usertype: " + usertype);
        System.out.println("Captcha: " + captcha);
        
        if (usertype.equals("admin")) {
            System.out.println("Processing admin login...");
            Admin admin = adminService.getByUsername(username);
            if (admin == null) {
                System.out.println("Admin not found: " + username);
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
            System.out.println("Processing customer login...");
            
            // 确保username不为空
            if (username == null || username.trim().equals("")) {
                resp.getWriter().print(ApiResult.json(false, "请输入客户编号"));
                return;
            }
            
            // 调用customerService前打印日志
            System.out.println("Calling customerService.getById(" + username + ")...");
            Customer customer = customerService.getById(username);
            
            if (customer == null) {
                System.out.println("Customer not found: " + username);
                resp.getWriter().print(ApiResult.json(false, "客户不存在，请检查客户编号是否正确"));
                return;
            }
            
            System.out.println("Customer found: id=" + customer.getId() + ", name=" + customer.getName() + ", password=" + customer.getPassword());
            
            // 密码比对：直接比对明文密码
            System.out.println("=== Password Comparison ===");
            System.out.println("Input password: " + password);
            System.out.println("Stored password: " + customer.getPassword());
            System.out.println("Passwords equal: " + password.equals(customer.getPassword()));
            
            // 尝试不同的比对方式，确保不会因为空格等问题导致比对失败
            String inputPassword = password != null ? password.trim() : "";
            String storedPassword = customer.getPassword() != null ? customer.getPassword().trim() : "";
            boolean passwordMatch = inputPassword.equals(storedPassword);
            System.out.println("Trimmed passwords equal: " + passwordMatch);
            
            if (passwordMatch) {
                req.getSession().setAttribute("user", customer);
                req.getSession().setAttribute("role", "customer");
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }
        } else if (usertype.equals("coach")) {
            System.out.println("Processing coach login...");
            
            // 确保username不为空
            if (username == null || username.trim().equals("")) {
                resp.getWriter().print(ApiResult.json(false, "请输入教练编号"));
                return;
            }
            
            // 调用coachService前打印日志
            System.out.println("Calling coachService.getById(" + username + ")...");
            Coach coach = coachService.getById(username);
            
            if (coach == null) {
                System.out.println("Coach not found: " + username);
                resp.getWriter().print(ApiResult.json(false, "教练不存在，请检查教练编号是否正确"));
                return;
            }
            
            System.out.println("Coach found: id=" + coach.getId() + ", name=" + coach.getName() + ", password=" + coach.getPassword());
            
            // 密码比对：直接比对明文密码
            System.out.println("=== Password Comparison ===");
            System.out.println("Input password: " + password);
            System.out.println("Stored password: " + coach.getPassword());
            System.out.println("Passwords equal: " + password.equals(coach.getPassword()));
            
            // 尝试不同的比对方式，确保不会因为空格等问题导致比对失败
            String inputPassword = password != null ? password.trim() : "";
            String storedPassword = coach.getPassword() != null ? coach.getPassword().trim() : "";
            boolean passwordMatch = inputPassword.equals(storedPassword);
            System.out.println("Trimmed passwords equal: " + passwordMatch);
            
            if (passwordMatch) {
                req.getSession().setAttribute("user", coach);
                req.getSession().setAttribute("role", "coach");
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }
        } else {
            System.out.println("Unknown usertype: " + usertype);
            resp.getWriter().print(ApiResult.json(false, "未知用户类型"));
            return;
        }
    }
}