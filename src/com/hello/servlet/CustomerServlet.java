package com.hello.servlet;


import com.hello.entity.Customer;
import com.hello.entity.Coach;
import com.hello.service.CustomerService;
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
import java.util.HashMap;
import java.util.List;

@WebServlet({"/customer", "/register"})
public class CustomerServlet extends HttpServlet {
   CustomerService customerService=new CustomerService();
   CoachService coachService=new CoachService();
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

            String id=req.getParameter("id");
            String gender=req.getParameter("gender");
            String coachId=req.getParameter("coachId");
            String name=req.getParameter("name");

            PagerVO<Customer> pagerVO=customerService.page(Integer.parseInt(current),10,id,name,gender,coachId);
            pagerVO.init();

            List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
            HashMap<String,Coach> coachmap=new HashMap<>();
                for (Coach coach : coaches) {
                    coachmap.put(coach.getId(), coach);
                }
                  for(Customer customer:pagerVO.getList()){
                      customer.setCoach(coachmap.get(customer.getCoachId()));
                    }
            req.setAttribute("id",id);
            req.setAttribute("gender",gender);
            req.setAttribute("coachId",coachId);
            req.setAttribute("name",name);
            req.setAttribute("coaches",coaches);
            req.setAttribute("pagerVO",pagerVO);
            req.getRequestDispatcher("/WEB-INF/view/customer-list.jsp").forward(req,resp);
        }
        if("add".equals(r)){
            boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
            return;
            }
            List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
            req.setAttribute("coaches",coaches);
            req.getRequestDispatcher("/WEB-INF/view/customer-add.jsp").forward(req,resp);
        }
        if("edit".equals(r)){
               boolean hasPermission=  MyUtils.hasPermission(req,resp,false,"admin");
            if(!hasPermission){
                return;
            }
            List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
            req.setAttribute("coaches",coaches);
            String id=req.getParameter("id");
            Customer customer=customerService.getById(id);
            req.setAttribute("entity",customer);

            req.getRequestDispatcher("/WEB-INF/view/customer-edit.jsp").forward(req,resp);
        }
        }



    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //新增 修改  删除
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");

        // 获取请求路径，判断是否为注册请求
        String servletPath = req.getServletPath();
        if("/register".equals(servletPath)){
            // 客户注册逻辑，不需要管理员权限
            Customer customer = new Customer();
            customer.setId(req.getParameter("sno"));
            customer.setPassword(req.getParameter("password"));
            customer.setName(req.getParameter("name"));
            customer.setTele(req.getParameter("tele"));
            customer.setGender(req.getParameter("gender"));
            customer.setAddress(req.getParameter("address"));
            customer.setCoachId(req.getParameter("clazzno"));
            String joindate = req.getParameter("enterdate");
            customer.setJoindate(MyUtils.strToDate(joindate));
            String age = req.getParameter("age");
            customer.setAge(Integer.parseInt(age));
            
            // 设置默认会员类型和余额
            customer.setMembershipType("普通会员");
            customer.setBalance(0.0);
            
            String msg = customerService.insert(customer);
            if(msg != null){
                resp.getWriter().write(ApiResult.json(false, msg));
            } else {
                resp.getWriter().write(ApiResult.json(true, "注册成功"));
                return;
            }
        } else {
            // 原有管理功能，需要管理员权限
            String r = req.getParameter("r");
            if("add".equals(r) || "edit".equals(r)){
                boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
                if(!hasPermission){
                    return;
                }
                Customer customer = new Customer();
                customer.setId(req.getParameter("id"));
                customer.setPassword(req.getParameter("password"));
                customer.setName(req.getParameter("name"));
                customer.setTele(req.getParameter("tele"));
                customer.setGender(req.getParameter("gender"));
                customer.setAddress(req.getParameter("address"));
                customer.setCoachId(req.getParameter("coachId"));
                String joindate = req.getParameter("joindate");
                customer.setJoindate(MyUtils.strToDate(joindate));
                String age = req.getParameter("age");
                customer.setAge(Integer.parseInt(age));
                
                customer.setMembershipType(req.getParameter("membershipType"));
                String expiryDate = req.getParameter("expiryDate");
                customer.setExpiryDate(MyUtils.strToDate(expiryDate));
                String balance = req.getParameter("balance");
                if(balance != null && !balance.equals("")){
                    customer.setBalance(Double.parseDouble(balance));
                }

                if("add".equals(r)){
                    String msg = customerService.insert(customer);
                    if(msg != null){
                        resp.getWriter().write(ApiResult.json(false, msg));
                    } else {
                        resp.getWriter().write(ApiResult.json(true, "保存客户成功"));
                        return;
                    }
                } else {
                    String msg = customerService.update(customer);
                    if(msg != null){
                        resp.getWriter().write(ApiResult.json(false, msg));
                        return;
                    } else {
                        resp.getWriter().write(ApiResult.json(true, "更新客户成功"));
                        return;
                    }
                }
            } else {
                //del
                //删除
                boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
                if(!hasPermission){
                    return;
                }
                String id = req.getParameter("id");
                int res = customerService.delete(id);
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
