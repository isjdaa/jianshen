package com.hello.servlet;


import com.hello.entity.Clazz;
import com.hello.entity.Student;
import com.hello.service.ClazzService;
import com.hello.service.StudentService;
import com.hello.utils.ApiResult;
import com.hello.utils.MyUtils;
import com.hello.utils.vo.PagerVO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@WebServlet("/student")
public class StudentServlet extends HttpServlet {
   StudentService studentService=new StudentService();
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

            String sno=req.getParameter("sno");
            String gender=req.getParameter("gender");
            String clazzno=req.getParameter("clazzno");
            String name=req.getParameter("name");

            PagerVO<Student> pagerVO=studentService.page(Integer.parseInt(current),10,sno,name,gender,clazzno);
            pagerVO.init();

            List<Clazz> clazzes =clazzService.listAll();
            HashMap<String,Clazz> clazzmap=new HashMap<>();
                for (Clazz clazz : clazzes) {
                    clazzmap.put(clazz.getClazzno(), clazz);
                }
                  for(Student student:pagerVO.getList()){
                      student.setClazz(clazzmap.get(student.getClazzno()));
                    }
            req.setAttribute("sno",sno);
            req.setAttribute("gender",gender);
            req.setAttribute("clazzno",clazzno);
            req.setAttribute("name",name);
            req.setAttribute("pagerVO",pagerVO);
            req.getRequestDispatcher("/WEB-INF/view/student-list.jsp").forward(req,resp);
        }
        if("add".equals(r)){
            boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
            return;
            }
            List<Clazz> clazzes =clazzService.listAll();
            req.setAttribute("clazzes",clazzes);
            req.getRequestDispatcher("/WEB-INF/view/student-add.jsp").forward(req,resp);
        }
        if("edit".equals(r)){
               boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
                return;
            }
            List<Clazz> clazzes =clazzService.listAll();
            req.setAttribute("clazzes",clazzes);
            String sno=req.getParameter("sno");
            Student student=studentService.getBySno(sno);
            req.setAttribute("entity",student);

            req.getRequestDispatcher("/WEB-INF/view/student-edit.jsp").forward(req,resp);
        }
        }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //新增 修改  删除
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

            String r=req.getParameter("r");
            if("add".equals(r) || "edit".equals(r)){
                boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
                if(!hasPermission){
                    return;
                }
                Student student=new Student();
                student.setSno(req.getParameter("sno"));
                student.setPassword(req.getParameter("password"));
                student.setName(req.getParameter("name"));
                student.setTele(req.getParameter("tele"));
                student.setGender(req.getParameter("gender"));
                student.setAddress(req.getParameter("address"));
                student.setClazzno(req.getParameter("clazzno"));
                String enterdate = req.getParameter("enterdate");
                student.setEnterdate(MyUtils.strToDate(enterdate));
                String age = req.getParameter("age");
                student.setAge(Integer.parseInt(age));

                if("add".equals(r)){
                    String msg = studentService.insert(student);
                    if(msg!=null){
                        resp.getWriter().write(ApiResult.json(false,msg));
                }else {
                        resp.getWriter().write(ApiResult.json(true, "保存学生成功"));
                        return;
                    }
                    }else{
                    String msg = studentService.update(student);
                    if(msg!=null){
                        resp.getWriter().write(ApiResult.json(false,msg));
                        return;
                }else{
                        resp.getWriter().write(ApiResult.json(true,"更新学生成功"));
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
                String sno= req.getParameter("sno");
                int res= studentService.delete(sno);
                if (res==0) {
                    resp.getWriter().write(ApiResult.json(false, "删除失败，请联系管理员"));
                    return;
                } else {
                    resp.getWriter().write(ApiResult.json(true, "删除成功"));
                    return;
                }
            }

            }
}
