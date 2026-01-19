package com.hello.servlet;

import com.hello.entity.Coach;
import com.hello.service.CoachService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet({"/coach", "/coach_register"})
public class CoachServlet extends HttpServlet {
   CoachService coachService = new CoachService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        //查询参数
        String r = req.getParameter("r");
        
        // 只有管理员能访问教练信息页面
        if(r == null || "add".equals(r) || "edit".equals(r)) {
            boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
            if (!hasPermission) {
                return;
            }
        }
        
        if (r == null) {
            String current = req.getParameter("current");
            if (current == null) {
                current = "1";
            }

            String id = req.getParameter("id");
            String name = req.getParameter("name");
            String gender = req.getParameter("gender");
            String specialization = req.getParameter("specialization");

            PagerVO<Coach> pagerVO = coachService.page(Integer.parseInt(current), 10, id, name, gender, specialization);
            pagerVO.init();

            req.setAttribute("id", id);
            req.setAttribute("name", name);
            req.setAttribute("gender", gender);
            req.setAttribute("specialization", specialization);
            req.setAttribute("pagerVO", pagerVO);
            req.getRequestDispatcher("/WEB-INF/view/coach-list.jsp").forward(req, resp);
        }
        if ("add".equals(r)) {
            boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
            if (!hasPermission) {
                return;
            }
            req.getRequestDispatcher("/WEB-INF/view/coach-add.jsp").forward(req, resp);
        }
        if ("edit".equals(r)) {
            boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
            if (!hasPermission) {
                return;
            }
            String id = req.getParameter("id");
            Coach coach = coachService.getById(id);
            req.setAttribute("entity", coach);
            req.getRequestDispatcher("/WEB-INF/view/coach-edit.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //新增 修改  删除
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        // 获取请求路径，判断是否为注册请求
        String servletPath = req.getServletPath();
        if("/coach_register".equals(servletPath)){
            // 教练注册逻辑，不需要管理员权限
            Coach coach = new Coach();
            coach.setId(req.getParameter("id"));
            coach.setPassword(req.getParameter("password"));
            coach.setName(req.getParameter("name"));
            coach.setTele(req.getParameter("tele"));
            coach.setSpecialization(req.getParameter("specialization"));
            coach.setGender(req.getParameter("gender"));
            String age = req.getParameter("age");
            coach.setAge(Integer.parseInt(age));
            coach.setAddress(req.getParameter("address"));
            
            String msg = coachService.insert(coach);
            if(msg != null){
                resp.getWriter().write(ApiResult.json(false, msg));
            } else {
                resp.getWriter().write(ApiResult.json(true, "注册成功"));
                return;
            }
        } else {
            // 原有管理功能，需要管理员权限
            String r = req.getParameter("r");
            if ("add".equals(r) || "edit".equals(r)) {
                boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
                if (!hasPermission) {
                    return;
                }
                Coach coach = new Coach();
                coach.setId(req.getParameter("id"));
                coach.setPassword(req.getParameter("password"));
                coach.setName(req.getParameter("name"));
                coach.setTele(req.getParameter("tele"));
                coach.setSpecialization(req.getParameter("specialization"));
                coach.setGender(req.getParameter("gender"));
                String age = req.getParameter("age");
                coach.setAge(Integer.parseInt(age));
                coach.setAddress(req.getParameter("address"));

                if ("add".equals(r)) {
                    String msg = coachService.insert(coach);
                    if (msg != null) {
                        resp.getWriter().write(ApiResult.json(false, msg));
                    } else {
                        resp.getWriter().write(ApiResult.json(true, "保存教练成功"));
                        return;
                    }
                } else {
                    String msg = coachService.update(coach);
                    if (msg != null) {
                        resp.getWriter().write(ApiResult.json(false, msg));
                        return;
                    } else {
                        resp.getWriter().write(ApiResult.json(true, "更新教练成功"));
                        return;
                    }
                }
            } else {
                //del
                //删除
                boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
                if (!hasPermission) {
                    return;
                }
                String id = req.getParameter("id");
                int res = coachService.delete(id);
                if (res == 0) {
                    resp.getWriter().write(ApiResult.json(false, "删除失败，请联系管理员"));
                    return;
                } else {
                    resp.getWriter().write(ApiResult.json(true, "删除成功"));
                    return;
                }
            }
        }
    }
}