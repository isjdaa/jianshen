package com.hello.servlet;

import com.hello.service.CustomerService;
import com.hello.service.CoachService;
import com.hello.utils.ApiResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/index")
public class IndexServlet extends HttpServlet {

    CustomerService customerService = new CustomerService();
    CoachService coachService = new CoachService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        int customerCount = customerService.count();
        int coachCount = coachService.count();
        Map<String,Object> res = new HashMap<>();
        res.put("customerCount", customerCount);
        res.put("coachCount", coachCount);
        resp.getWriter().write(ApiResult.json(true,"成功",res));
    }
}
