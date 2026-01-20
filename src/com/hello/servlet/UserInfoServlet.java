package com.hello.servlet;

import com.hello.dao.CustomerDAO;
import com.hello.dao.CoachDAO;
import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/userinfo"})
public class UserInfoServlet extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();
    private CoachDAO coachDAO = new CoachDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        // 获取当前登录用户
        Object userObj = req.getSession().getAttribute("user");
        String role = (String) req.getSession().getAttribute("role");

        if (userObj == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        // 跳转到个人信息页面
        req.getRequestDispatcher("/WEB-INF/view/userinfo.jsp").forward(req, resp);
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
            String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
            String tele = req.getParameter("tele") != null ? req.getParameter("tele").trim() : "";
            String gender = req.getParameter("gender") != null ? req.getParameter("gender").trim() : "";
            String address = req.getParameter("address") != null ? req.getParameter("address").trim() : "";
            String avatar = req.getParameter("avatar") != null ? req.getParameter("avatar").trim() : "";

            if (name.equals("")) {
                apiResult.error("姓名不能为空");
                out.print(apiResult.toJson());
                out.close();
                return;
            }

            if (tele.equals("")) {
                apiResult.error("电话不能为空");
                out.print(apiResult.toJson());
                out.close();
                return;
            }
            
            // 限制头像大小，Base64字符串长度约为原始文件大小的1.37倍
            if (!avatar.equals("")) {
                // 限制为2MB左右的图片（Base64约2.7MB）
                if (avatar.length() > 3 * 1024 * 1024) {
                    apiResult.error("头像大小不能超过2MB");
                    out.print(apiResult.toJson());
                    out.close();
                    return;
                }
            }

            int result = 0;
            if ("customer".equals(role)) {
                // 更新客户信息
                Customer customer = (Customer) userObj;
                customer.setName(name);
                customer.setTele(tele);
                customer.setGender(gender);
                customer.setAddress(address);
                if (!avatar.equals("")) {
                    customer.setAvatar(avatar);
                }
                result = customerDAO.update(customer);
            } else if ("coach".equals(role)) {
                // 更新教练信息
                Coach coach = (Coach) userObj;
                coach.setName(name);
                coach.setTele(tele);
                coach.setGender(gender);
                coach.setAddress(address);
                if (!avatar.equals("")) {
                    coach.setAvatar(avatar);
                }
                // 获取教练特有的参数
                String specialization = req.getParameter("specialization") != null ? req.getParameter("specialization").trim() : "";
                if (!specialization.equals("")) {
                    coach.setSpecialization(specialization);
                }
                result = coachDAO.update(coach);
            }

            if (result > 0) {
                // 更新会话中的用户信息
                req.getSession().setAttribute("user", userObj);
                apiResult.success("个人信息更新成功");
            } else {
                apiResult.error("个人信息更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("更新失败：" + e.getMessage());
        }

        out.print(apiResult.toJson());
        out.flush();
        out.close();
    }
}