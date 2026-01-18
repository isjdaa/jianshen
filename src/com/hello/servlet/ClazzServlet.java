package com.hello.servlet;


import com.hello.entity.Clazz;
import com.hello.service.ClazzService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/clazz")
public class ClazzServlet extends HttpServlet {
   ClazzService clazzService=new ClazzService();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        //查询参数
        String r=req.getParameter("r");
        if(r == null){
            String current=req.getParameter("current");
            if(current==null){
                current="1";
            }
            String clazzno=req.getParameter("clazzno");
            String name=req.getParameter("name");

            PagerVO<Clazz> pagerVO=clazzService.page(Integer.parseInt(current),10,clazzno,name);
            pagerVO.init();
            req.setAttribute("clazzno",clazzno);
            req.setAttribute("name",name);
            req.setAttribute("pagerVO",pagerVO);
            req.getRequestDispatcher("/WEB-INF/view/clazz-list.jsp").forward(req,resp);
        }
        if("add".equals(r)){
            boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
                return;
            }
            req.getRequestDispatcher("/WEB-INF/view/clazz-add.jsp").forward(req,resp);
        }
        if("edit".equals(r)){
            boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
                return;
            }
            String clazzno=req.getParameter("clazzno");
            Clazz clazz=clazzService.getByClazzno(clazzno);
            req.setAttribute("entity",clazz);
            req.getRequestDispatcher("/WEB-INF/view/clazz-edit.jsp").forward(req,resp);
        }
        }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //新增 修改  删除
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        //
            String r=req.getParameter("r");
            if("add".equals(r) || "edit".equals(r)){
                boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
                if(!hasPermission){
                    return;
                }
                String clazzno=req.getParameter("clazzno");
                String name =req.getParameter("name");
                Clazz clazz=new Clazz();
                clazz.setName(name);
                clazz.setClazzno(clazzno);
                if("add".equals(r)){
                    String msg = clazzService.insert(clazz);
                    if(msg!=null){
                        resp.getWriter().write(ApiResult.json(false,msg));
                }else {
                        resp.getWriter().write(ApiResult.json(true, "保存班级成功"));
                        return;
                    }
                    }else{
                    String msg = clazzService.update(clazz);
                    if(msg!=null){
                        resp.getWriter().write(ApiResult.json(false,msg));
                        return;
                }else{
                        resp.getWriter().write(ApiResult.json(true,"保存班级成功"));
                    return;
                    }
                    }
            }else {
                //del
                //删除
                boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
                if(!hasPermission){
                    return;
                }
                String clazzno = req.getParameter("clazzno");
                String msg = clazzService.delete(clazzno);
                if (msg != null) {
                    resp.getWriter().write(ApiResult.json(false, msg));
                    return;
                } else {
                    resp.getWriter().write(ApiResult.json(true, "删除成功"));
                    return;
                }
            }

            }
}
