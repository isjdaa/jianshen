package com.hello.servlet;

import com.hello.entity.Member;
import com.hello.service.MemberService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/member")
public class MemberServlet extends HttpServlet {
    MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        String r = req.getParameter("r");

        if ("add".equals(r) || "edit".equals(r) || "self_register".equals(r)) {
            boolean requireAdmin = !"self_register".equals(r);
            boolean hasPermission = MyUtils.hasPermission(req, resp, requireAdmin, "admin");
            if (!hasPermission) return;

            Member m = new Member();
            m.setId(req.getParameter("id"));
            m.setPassword(req.getParameter("password"));
            m.setName(req.getParameter("name"));
            m.setTele(req.getParameter("tele"));
            m.setGender(req.getParameter("gender"));
            m.setAddress(req.getParameter("address"));
            m.setTrainerNo(req.getParameter("trainerNo"));
            String joindate = req.getParameter("joindate");
            if (joindate != null && !joindate.isEmpty()) {
                m.setJoindate(MyUtils.strToDate(joindate));
            } else {
                m.setJoindate(new Date());
            }
            String age = req.getParameter("age");
            if (age != null && !age.isEmpty()) m.setAge(Integer.parseInt(age));
            m.setMembershipType(req.getParameter("membershipType"));
            String expiry = req.getParameter("expiryDate");
            if (expiry != null && !expiry.isEmpty()) m.setExpiryDate(MyUtils.strToDate(expiry));
            String balance = req.getParameter("balance");
            if (balance != null && !balance.isEmpty()) m.setBalance(Double.parseDouble(balance));
            else m.setBalance(0.0);

            boolean ok;
            if ("add".equals(r) || "self_register".equals(r)) ok = memberService.add(m);
            else ok = memberService.update(m);

            if (ok) resp.getWriter().write(ApiResult.success("保存成功").toJSONString());
            else resp.getWriter().write(ApiResult.fail("保存失败").toJSONString());
            return;
        }

        if ("delete".equals(r)) {
            boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
            if (!hasPermission) return;
            String id = req.getParameter("id");
            boolean ok = memberService.delete(id);
            if (ok) resp.getWriter().write(ApiResult.success("删除成功").toJSONString());
            else resp.getWriter().write(ApiResult.fail("删除失败").toJSONString());
            return;
        }

        // 查询 & 分页（可复用原学生分页逻辑），如需我可以把 pager 查询的完整实现也加入
    }
}