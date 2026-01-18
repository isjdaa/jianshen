package com.hello.servlet;

import com.hello.entity.Admin;
import com.hello.entity.Student;
import com.hello.service.AdminService;
import com.hello.service.StudentService;
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
    StudentService studentService = new StudentService();

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

        } else {
            Student student = studentService.getBySno(username);
            if (student == null) {
                resp.getWriter().print(ApiResult.json(false, "用户不存在"));
                return;
            }
            // ============核心修改：仅这一行！学生密码做MD5加密后再对比============
            if (student.getPassword().equals(MD5Util.md5Encode(password))) {
                req.getSession().setAttribute("user", student);
                req.getSession().setAttribute("role", "student");
                resp.getWriter().print(ApiResult.json(true, "登陆成功"));
                return;
            } else {
                resp.getWriter().print(ApiResult.json(false, "密码错误"));
                return;
            }
        }

    }
}