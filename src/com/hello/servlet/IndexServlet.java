package com.hello.servlet;

import com.hello.entity.Clazz;
import com.hello.service.ClazzService;
import com.hello.service.StudentService;
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

    ClazzService clazzService=new ClazzService();
    StudentService studentService=new StudentService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        int clazzC=clazzService.count();
        int stuC=studentService.count();
        List<Clazz> clazzes=clazzService.statistics();
        Map<String,Object> res=new HashMap<>();
        res.put("clazzCount",clazzC);
        res.put("studentCount",stuC);
        res.put("clazzes",clazzes);
        resp.getWriter().write(ApiResult.json(true,"成功",res));
    }
}
