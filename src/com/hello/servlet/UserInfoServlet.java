package com.hello.servlet;

import com.hello.dao.CustomerDAO;
import com.hello.dao.CoachDAO;
import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

// 新增 MultipartConfig 支持文件上传
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50    // 50MB
)
@WebServlet({"/userinfo"})
public class UserInfoServlet extends HttpServlet {
    private CustomerDAO customerDAO = new CustomerDAO();
    private CoachDAO coachDAO = new CoachDAO();
    // 头像存储路径（相对于项目根目录）
    private static final String AVATAR_UPLOAD_PATH = "uploads/avatars/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html; charset=utf-8");

        Object userObj = req.getSession().getAttribute("user");
        String role = (String) req.getSession().getAttribute("role");

        if (userObj == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        req.getRequestDispatcher("/WEB-INF/view/userinfo.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

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
            // 1. 获取普通表单参数
            String name = req.getParameter("name") != null ? req.getParameter("name").trim() : "";
            String tele = req.getParameter("tele") != null ? req.getParameter("tele").trim() : "";
            String gender = req.getParameter("gender") != null ? req.getParameter("gender").trim() : "";
            String address = req.getParameter("address") != null ? req.getParameter("address").trim() : "";

            // 参数校验
            if (name.isEmpty()) {
                apiResult.error("姓名不能为空");
                out.print(apiResult.toJson());
                return;
            }
            if (tele.isEmpty()) {
                apiResult.error("电话不能为空");
                out.print(apiResult.toJson());
                return;
            }

            // 2. 处理头像上传
            String avatarPath = null;
            Part filePart = req.getPart("avatarFile"); // 对应前端文件上传的name属性
            if (filePart != null && filePart.getSize() > 0) {
                // 获取文件扩展名
                String fileName = filePart.getSubmittedFileName();
                String ext = fileName.substring(fileName.lastIndexOf("."));
                // 生成唯一文件名避免重复
                String newFileName = UUID.randomUUID().toString() + ext;

                // 创建上传目录（如果不存在）
                String realUploadPath = getServletContext().getRealPath("/" + AVATAR_UPLOAD_PATH);
                File uploadDir = new File(realUploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 保存文件到服务器
                String filePath = realUploadPath + File.separator + newFileName;
                filePart.write(filePath);

                // 构建访问路径（前端可访问的URL）
                avatarPath = req.getContextPath() + "/" + AVATAR_UPLOAD_PATH + newFileName;
                System.out.println("保存的头像URL: " + avatarPath);
            }

            // 3. 更新用户信息
            int result = 0;
            if ("customer".equals(role)) {
                Customer customer = (Customer) userObj;
                customer.setName(name);
                customer.setTele(tele);
                customer.setGender(gender);
                customer.setAddress(address);
                // 只有上传了新头像才更新
                if (avatarPath != null) {
                    customer.setAvatar(avatarPath);
                }
                result = customerDAO.update(customer);
            } else if ("coach".equals(role)) {
                Coach coach = (Coach) userObj;
                coach.setName(name);
                coach.setTele(tele);
                coach.setGender(gender);
                coach.setAddress(address);
                String specialization = req.getParameter("specialization") != null ? req.getParameter("specialization").trim() : "";
                coach.setSpecialization(specialization);
                // 只有上传了新头像才更新
                if (avatarPath != null) {
                    coach.setAvatar(avatarPath);
                }
                result = coachDAO.update(coach);
            }

            // 4. 同步Session中的用户信息
            if (result > 0) {
                if ("customer".equals(role)) {
                    Customer updatedCustomer = customerDAO.getById(((Customer) userObj).getId());
                    req.getSession().setAttribute("user", updatedCustomer);
                } else if ("coach".equals(role)) {
                    Coach updatedCoach = coachDAO.getById(((Coach) userObj).getId());
                    req.getSession().setAttribute("user", updatedCoach);
                }
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