package com.hello.servlet;

import com.hello.dao.AppointmentDAO;
import com.hello.dao.CourseDAO;
import com.hello.dao.CourseAppointmentDAO;
import com.hello.entity.Appointment;
import com.hello.entity.Course;
import com.hello.entity.CourseAppointment;
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
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@WebServlet({"/appointment/*"})
public class AppointmentServlet extends HttpServlet {
    private AppointmentDAO appointmentDAO = new AppointmentDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private CourseAppointmentDAO courseAppointmentDAO = new CourseAppointmentDAO();
    private CustomerService customerService = new CustomerService();
    private CoachService coachService = new CoachService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendRedirect(req.getContextPath() + "/appointment/my");
            return;
        }

        // 预约教练页面
        if (pathInfo.equals("/coach")) {
            handleCoachAppointment(req, resp);
        }
        // 预约课程页面
        else if (pathInfo.equals("/course")) {
            handleCourseAppointment(req, resp);
        }
        // 我的预约页面
        else if (pathInfo.equals("/my")) {
            handleMyAppointments(req, resp);
        }
        // 教练查看预约页面
        else if (pathInfo.equals("/coach/view")) {
            handleCoachViewAppointments(req, resp);
        }
        // 管理员查看所有预约页面
        else if (pathInfo.equals("/admin/list")) {
            handleAdminAppointmentList(req, resp);
        }
        // 教练查看自己的课程安排
        else if (pathInfo.equals("/coach/courses")) {
            handleCoachCourses(req, resp);
        }
        // 管理员查看所有课程安排
        else if (pathInfo.equals("/admin/courses")) {
            handleAdminCourses(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        String pathInfo = req.getPathInfo();
        ApiResult apiResult = new ApiResult();

        // 预约教练
        if (pathInfo.equals("/coach")) {
            apiResult = createCoachAppointment(req);
        }
        // 预约课程
        else if (pathInfo.equals("/course")) {
            apiResult = createCourseAppointment(req);
        }
        // 更新预约状态
        else if (pathInfo.equals("/updateStatus")) {
            apiResult = updateAppointmentStatus(req);
        }

        out.print(apiResult.toJson());
        out.flush();
        out.close();
    }

    // 处理教练预约页面
    private void handleCoachAppointment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 客户才能预约教练
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "customer");
        if (!hasPermission) {
            return;
        }

        // 获取所有教练
        List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
        req.setAttribute("coaches", coaches);
        req.getRequestDispatcher("/WEB-INF/view/appointment-coach.jsp").forward(req, resp);
    }

    // 处理课程预约页面
    private void handleCourseAppointment(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 客户才能预约课程
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "customer");
        if (!hasPermission) {
            return;
        }

        // 获取所有课程
        PagerVO<Course> coursePager = courseDAO.findAll(1, 100);
        List<Course> courses = coursePager.getList();

        // 获取课程对应的教练信息
        HashMap<String, Coach> coachMap = new HashMap<>();
        List<Coach> allCoaches = coachService.page(1, 100, null, null, null, null).getList();
        for (Coach coach : allCoaches) {
            coachMap.put(coach.getId(), coach);
        }

        for (Course course : courses) {
            course.setCoach(coachMap.get(course.getCoachId()));
        }

        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/view/appointment-course.jsp").forward(req, resp);
    }

    // 处理我的预约页面 - 修复核心BUG：从Session中获取正确的用户对象和ID
    private void handleMyAppointments(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ========== 修复BUG2 核心：获取登录用户的正确ID ==========
        Object userObj = req.getSession().getAttribute("user");
        String role = (String) req.getSession().getAttribute("role");
        String userId = null;

        if(userObj == null){
            resp.sendRedirect(req.getContextPath()+"/login.jsp");
            return;
        }

        // 获取分页参数
        int currentPage = 1;
        int pageSize = 10;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        if ("customer".equals(role)) {
            userId = ((Customer)userObj).getId();
            // 客户查看自己的预约
            PagerVO<Appointment> appointmentPager = appointmentDAO.findByCustomerId(currentPage, pageSize, userId);
            PagerVO<CourseAppointment> courseAppointmentPager = courseAppointmentDAO.findByCustomerId(currentPage, pageSize, userId);

            // 初始化分页属性
            appointmentPager.init();
            courseAppointmentPager.init();

            // 获取关联的教练和课程信息
            loadAppointmentAssociations(appointmentPager, courseAppointmentPager);

            req.setAttribute("appointmentPager", appointmentPager);
            req.setAttribute("courseAppointmentPager", courseAppointmentPager);
            req.setAttribute("appointments", appointmentPager.getList());
            req.setAttribute("courseAppointments", courseAppointmentPager.getList());
        } else if ("coach".equals(role)) {
            userId = ((Coach)userObj).getId();
            // 教练查看自己的预约
            PagerVO<Appointment> appointmentPager = appointmentDAO.findByCoachId(currentPage, pageSize, userId);

            // 初始化分页属性
            appointmentPager.init();

            // 获取关联的客户信息
            loadAppointmentCustomerInfo(appointmentPager);

            req.setAttribute("appointmentPager", appointmentPager);
            req.setAttribute("appointments", appointmentPager.getList());
        }

        req.getRequestDispatcher("/WEB-INF/view/appointment-my.jsp").forward(req, resp);
    }

    // 处理教练查看预约页面 - 修复核心BUG：获取正确教练ID
    private void handleCoachViewAppointments(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 教练才能查看自己的预约
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "coach");
        if (!hasPermission) {
            return;
        }

        // ========== 修复BUG2 核心：获取登录教练的正确ID ==========
        Coach coach = (Coach) req.getSession().getAttribute("user");
        String userId = coach.getId();

        // 获取分页参数
        int currentPage = 1;
        int pageSize = 10;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        PagerVO<Appointment> appointmentPager = appointmentDAO.findByCoachId(currentPage, pageSize, userId);
        // 初始化分页属性
        appointmentPager.init();

        // 获取关联的客户信息
        loadAppointmentCustomerInfo(appointmentPager);

        req.setAttribute("appointmentPager", appointmentPager);
        req.setAttribute("appointments", appointmentPager.getList());
        req.getRequestDispatcher("/WEB-INF/view/appointment-coach-view.jsp").forward(req, resp);
    }
    
    // 处理管理员查看所有预约页面
    private void handleAdminAppointmentList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 只有管理员才能查看所有预约
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
        if (!hasPermission) {
            return;
        }

        // 获取分页参数
        int currentPage = 1;
        int pageSize = 10;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // 查询所有预约
        PagerVO<Appointment> appointmentPager = appointmentDAO.findAll(currentPage, pageSize);
        // 初始化分页属性
        appointmentPager.init();

        // 获取关联的客户和教练信息
        loadAppointmentAssociations(appointmentPager, null);

        req.setAttribute("appointmentPager", appointmentPager);
        req.setAttribute("appointments", appointmentPager.getList());
        req.getRequestDispatcher("/WEB-INF/view/appointment-admin-list.jsp").forward(req, resp);
    }
    
    // 处理教练查看自己的课程安排页面
    private void handleCoachCourses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 只有教练才能查看自己的课程
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "coach");
        if (!hasPermission) {
            return;
        }

        // 获取当前登录的教练
        Coach coach = (Coach) req.getSession().getAttribute("user");
        String coachId = coach.getId();

        // 获取分页参数
        int currentPage = 1;
        int pageSize = 10;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // 查询该教练的所有课程
        PagerVO<Course> coursePager = courseDAO.findByCoachId(currentPage, pageSize, coachId);
        // 初始化分页属性
        coursePager.init();

        req.setAttribute("coursePager", coursePager);
        req.setAttribute("courses", coursePager.getList());
        req.getRequestDispatcher("/WEB-INF/view/appointment-coach-courses.jsp").forward(req, resp);
    }
    
    // 处理管理员查看所有课程安排页面
    private void handleAdminCourses(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 只有管理员才能查看所有课程
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin");
        if (!hasPermission) {
            return;
        }

        // 获取分页参数
        int currentPage = 1;
        int pageSize = 10;
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }

        // 查询所有课程
        PagerVO<Course> coursePager = courseDAO.findAll(currentPage, pageSize);
        // 初始化分页属性
        coursePager.init();

        // 获取所有教练信息，用于关联课程的教练
        List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
        HashMap<String, Coach> coachMap = new HashMap<>();
        for (Coach coach : coaches) {
            coachMap.put(coach.getId(), coach);
        }

        // 为每个课程添加教练信息
        List<Course> courses = coursePager.getList();
        for (Course course : courses) {
            course.setCoach(coachMap.get(course.getCoachId()));
        }

        // 自动更新课程状态
        updateCourseStatuses(courses);

        req.setAttribute("coursePager", coursePager);
        req.setAttribute("courses", courses);
        req.getRequestDispatcher("/WEB-INF/view/appointment-admin-courses.jsp").forward(req, resp);
    }

    // 创建教练预约 - 修复BUG1+BUG2+日期校验+所有逻辑漏洞
    private ApiResult createCoachAppointment(HttpServletRequest req) {
        ApiResult apiResult = new ApiResult();

        try {
            // ========== 修复BUG2 核心：获取登录客户的正确ID ==========
            Object userObj = req.getSession().getAttribute("user");
            if (userObj == null || !(userObj instanceof Customer)) {
                return apiResult.error("请先登录客户账号");
            }
            String customerId = ((Customer)userObj).getId();

            // 获取请求参数 并做非空+去空格处理
            String coachId = req.getParameter("coachId")!=null?req.getParameter("coachId").trim():"";
            String appointmentDate = req.getParameter("appointmentDate")!=null?req.getParameter("appointmentDate").trim():"";
            String appointmentTime = req.getParameter("appointmentTime")!=null?req.getParameter("appointmentTime").trim():"";
            String remarks = req.getParameter("remarks")!=null?req.getParameter("remarks").trim():"";

            // 参数校验
            if(coachId.equals("") || appointmentDate.equals("") || appointmentTime.equals("")){
                return apiResult.error("教练、预约日期、预约时间为必填项！");
            }

            // ========== 修复BUG1 核心：使用项目工具类转换日期，解决500报错 ==========
            Date appDate = MyUtils.strToDate(appointmentDate);
            if(appDate == null){
                return apiResult.error("预约日期格式不正确！");
            }

            // 不能预约过去的日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date today = cal.getTime();
            if(appDate.before(today)){
                return apiResult.error("不能预约过去的日期，请选择正确的预约日期！");
            }

            // 不能预约太远的日期（最多提前30天）
            Date maxDate = new Date();
            maxDate.setTime(today.getTime() + 30L * 24 * 60 * 60 * 1000);
            if(appDate.after(maxDate)){
                return apiResult.error("最多只能提前30天预约，请选择合适的预约日期！");
            }

            // 检查是否重复预约（同一个客户不能在同一时间预约同一个教练）
            if (appointmentDAO.hasDuplicateAppointment(customerId, coachId, appDate, appointmentTime, null)) {
                return apiResult.error("您在该时间段已经预约过该教练，请选择其他时间或教练！");
            }

            // 检查教练时间冲突（已确认的预约不能有时间冲突）
            if (appointmentDAO.hasTimeConflict(coachId, appDate, appointmentTime, null)) {
                return apiResult.error("该教练在该时间段已被预约，请选择其他时间！");
            }

            // 创建预约对象
            Appointment appointment = new Appointment();
            appointment.setId(UUID.randomUUID().toString().replace("-", ""));
            appointment.setCustomerId(customerId);
            appointment.setCoachId(coachId);
            appointment.setAppointmentDate(appDate);
            appointment.setAppointmentTime(appointmentTime);
            appointment.setStatus("pending"); // 待确认
            appointment.setCreateTime(new Date());
            appointment.setUpdateTime(new Date());
            appointment.setRemarks(remarks);

            // 保存预约
            int result = appointmentDAO.insert(appointment);
            if (result > 0) {
                apiResult.success("教练预约提交成功，等待教练确认！");
            } else {
                apiResult.error("教练预约提交失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("预约失败：" + e.getMessage());
        }

        return apiResult;
    }

    // 创建课程预约 - 修复BUG2+业务逻辑漏洞+课程时间赋值错误
    private ApiResult createCourseAppointment(HttpServletRequest req) {
        ApiResult apiResult = new ApiResult();

        try {
            // ========== 修复BUG2 核心：获取登录客户的正确ID ==========
            Object userObj = req.getSession().getAttribute("user");
            if (userObj == null || !(userObj instanceof Customer)) {
                return apiResult.error("请先登录客户账号");
            }
            String customerId = ((Customer)userObj).getId();

            // 获取请求参数
            String courseId = req.getParameter("courseId")!=null?req.getParameter("courseId").trim():"";
            if(courseId.equals("")){
                return apiResult.error("请选择要预约的课程！");
            }

            // 检查课程是否存在
            Course course = courseDAO.getById(courseId);
            if (course == null) {
                return apiResult.error("该课程不存在或已下架！");
            }

            // 检查课程是否已满
            if (course.getCurrentStudents() >= course.getMaxStudents()) {
                return apiResult.error("该课程名额已满，无法预约！");
            }

            // 检查课程是否已经开始或结束
            Date now = new Date();
            if (course.getCourseTime().before(now)) {
                return apiResult.error("该课程已经开始或结束，无法预约！");
            }

            // 检查课程是否在可预约时间范围内（至少提前1小时预约）
            Date minAppointmentTime = new Date(now.getTime() + 60 * 60 * 1000); // 1小时后
            if (course.getCourseTime().before(minAppointmentTime)) {
                return apiResult.error("该课程即将开始，无法预约！请至少提前1小时预约。");
            }

            // 检查客户是否已经预约过该课程
            if (courseAppointmentDAO.hasDuplicateCourseAppointment(customerId, courseId, null)) {
                return apiResult.error("您已经预约过该课程，请勿重复预约！");
            }

            // ========== 修复业务逻辑漏洞：预约时间赋值为【课程本身的上课时间】而非系统时间 ==========
            CourseAppointment courseAppointment = new CourseAppointment();
            courseAppointment.setId(UUID.randomUUID().toString().replace("-", ""));
            courseAppointment.setCustomerId(customerId);
            courseAppointment.setCourseId(courseId);
            courseAppointment.setAppointmentTime(course.getCourseTime()); // 正确赋值课程时间
            courseAppointment.setStatus("confirmed"); // 课程预约默认已确认
            courseAppointment.setCreateTime(new Date());

            // 保存预约
            int result = courseAppointmentDAO.insert(courseAppointment);
            if (result > 0) {
                // 更新课程当前人数
                courseDAO.updateCurrentStudents(courseId, 1);
                apiResult.success("课程预约成功！");
            } else {
                apiResult.error("课程预约失败，请稍后重试");
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("预约失败：" + e.getMessage());
        }

        return apiResult;
    }

    // 更新预约状态 - 修复BUG5：取消课程预约防负数+状态校验
    private ApiResult updateAppointmentStatus(HttpServletRequest req) {
        ApiResult apiResult = new ApiResult();

        try {
            String id = req.getParameter("id")!=null?req.getParameter("id").trim():"";
            String status = req.getParameter("status")!=null?req.getParameter("status").trim():"";
            String type = req.getParameter("type")!=null?req.getParameter("type").trim():"";

            if (id.equals("") || status.equals("") || type.equals("")) {
                return apiResult.error("参数不能为空，请刷新页面重试！");
            }

            int result = 0;
            if ("appointment".equals(type)) {
                // 更新教练预约状态 + 状态转换验证
                Appointment appointment = appointmentDAO.getById(id);
                if (appointment == null) {
                    return apiResult.error("该预约记录不存在！");
                }

                // 状态转换验证
                String currentStatus = appointment.getStatus();
                if (!isValidStatusTransition(currentStatus, status, type)) {
                    return apiResult.error("无效的状态转换操作！");
                }

                // 检查时间限制：只能拦截“早于今天”的预约（避免把“今天(00:00)”误判为已过去）
                Calendar calNow = Calendar.getInstance();
                calNow.setTime(new Date());
                calNow.set(Calendar.HOUR_OF_DAY, 0);
                calNow.set(Calendar.MINUTE, 0);
                calNow.set(Calendar.SECOND, 0);
                calNow.set(Calendar.MILLISECOND, 0);
                Date todayStart = calNow.getTime();
                if (appointment.getAppointmentDate() != null
                        && appointment.getAppointmentDate().before(todayStart)
                        && !"cancelled".equals(status)) {
                    return apiResult.error("已过去的预约只能取消，不能修改其他状态！");
                }

                result = appointmentDAO.updateStatus(id, status);
            } else if ("courseAppointment".equals(type)) {
                // 更新课程预约状态
                CourseAppointment courseAppointment = courseAppointmentDAO.getById(id);
                if(courseAppointment == null){
                    return apiResult.error("该课程预约记录不存在！");
                }

                // 状态转换验证
                String currentStatus = courseAppointment.getStatus();
                if (!isValidStatusTransition(currentStatus, status, type)) {
                    return apiResult.error("无效的状态转换操作！");
                }

                // 检查课程时间：课程开始后不能取消预约
                Course course = courseDAO.getById(courseAppointment.getCourseId());
                if (course != null && course.getCourseTime().before(new Date()) && "cancelled".equals(status)) {
                    return apiResult.error("课程已经开始，不能取消预约！");
                }

                // ========== 修复BUG5：防止取消预约导致人数负数 ==========
                if ("cancelled".equals(status) && "confirmed".equals(courseAppointment.getStatus())) {
                    courseDAO.updateCurrentStudents(courseAppointment.getCourseId(), -1);
                }
                result = courseAppointmentDAO.updateStatus(id, status);
            }

            if (result > 0) {
                apiResult.success("状态更新成功！");
            } else {
                apiResult.error("状态更新失败，该记录可能已被处理！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("更新失败：" + e.getMessage());
        }

        return apiResult;
    }

    // 加载预约关联信息
    private void loadAppointmentAssociations(PagerVO<Appointment> appointmentPager, PagerVO<CourseAppointment> courseAppointmentPager) {
        if (appointmentPager != null && appointmentPager.getList() != null) {
            List<Customer> customers = customerService.page(1, 100, null, null, null, null).getList();
            List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();

            HashMap<String, Customer> customerMap = new HashMap<>();
            HashMap<String, Coach> coachMap = new HashMap<>();

            for (Customer customer : customers) {
                customerMap.put(customer.getId(), customer);
            }

            for (Coach coach : coaches) {
                coachMap.put(coach.getId(), coach);
            }

            for (Appointment appointment : appointmentPager.getList()) {
                appointment.setCustomer(customerMap.get(appointment.getCustomerId()));
                appointment.setCoach(coachMap.get(appointment.getCoachId()));
            }
        }

        if (courseAppointmentPager != null && courseAppointmentPager.getList() != null) {
            List<Customer> customers = customerService.page(1, 100, null, null, null, null).getList();
            List<Course> courses = courseDAO.findAll(1, 100).getList();
            List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();

            HashMap<String, Customer> customerMap = new HashMap<>();
            HashMap<String, Course> courseMap = new HashMap<>();
            HashMap<String, Coach> coachMap = new HashMap<>();

            for (Customer customer : customers) {
                customerMap.put(customer.getId(), customer);
            }

            for (Course course : courses) {
                courseMap.put(course.getId(), course);
            }

            for (Coach coach : coaches) {
                coachMap.put(coach.getId(), coach);
            }

            for (CourseAppointment courseAppointment : courseAppointmentPager.getList()) {
                courseAppointment.setCustomer(customerMap.get(courseAppointment.getCustomerId()));
                Course course = courseMap.get(courseAppointment.getCourseId());
                if (course != null) {
                    course.setCoach(coachMap.get(course.getCoachId()));
                    courseAppointment.setCourse(course);
                }
            }
        }
    }

    // 加载预约客户信息
    private void loadAppointmentCustomerInfo(PagerVO<Appointment> appointmentPager) {
        if (appointmentPager != null && appointmentPager.getList() != null) {
            List<Customer> customers = customerService.page(1, 100, null, null, null, null).getList();
            HashMap<String, Customer> customerMap = new HashMap<>();

            for (Customer customer : customers) {
                customerMap.put(customer.getId(), customer);
            }

            for (Appointment appointment : appointmentPager.getList()) {
                appointment.setCustomer(customerMap.get(appointment.getCustomerId()));
            }
        }
    }

    // 状态转换验证方法
    private boolean isValidStatusTransition(String currentStatus, String newStatus, String type) {
        // 统一将状态值转换为英文
        String normalizedCurrentStatus = normalizeStatus(currentStatus);
        String normalizedNewStatus = normalizeStatus(newStatus);
        
        if ("appointment".equals(type)) {
            // 教练预约状态转换规则
            switch (normalizedCurrentStatus) {
                case "pending":
                    return "confirmed".equals(normalizedNewStatus) || "cancelled".equals(normalizedNewStatus);
                case "confirmed":
                    return "completed".equals(normalizedNewStatus) || "cancelled".equals(normalizedNewStatus);
                case "completed":
                case "cancelled":
                    return false; // 已完成或已取消的不能再修改
                default:
                    return false;
            }
        } else if ("courseAppointment".equals(type)) {
            // 课程预约状态转换规则
            switch (normalizedCurrentStatus) {
                case "confirmed":
                    return "attended".equals(normalizedNewStatus) || "cancelled".equals(normalizedNewStatus);
                case "attended":
                case "cancelled":
                    return false; // 已参加或已取消的不能再修改
                default:
                    return false;
            }
        }
        return false;
    }
    
    // 状态值标准化方法，将中英文状态值转换为英文
    private String normalizeStatus(String status) {
        if (status == null) {
            return null;
        }
        status = status.trim();
        switch (status) {
            case "待确认":
                return "pending";
            case "已确认":
                return "confirmed";
            case "已完成":
                return "completed";
            case "已取消":
                return "cancelled";
            default:
                return status;
        }
    }

    // 自动更新课程状态
    private void updateCourseStatuses(List<Course> courses) {
        Date now = new Date();
        long currentTime = now.getTime();
        long thirtyMinutesInMillis = 30 * 60 * 1000; // 30分钟（毫秒）

        for (Course course : courses) {
            if (course.getCourseTime() != null) {
                long courseTime = course.getCourseTime().getTime();
                long timeDiff = courseTime - currentTime;

                // 如果课程时间在当前时间之前，则状态为"已完成"
                if (timeDiff < 0) {
                    if (!"已完成".equals(course.getStatus())) {
                        course.setStatus("已完成");
                        courseDAO.update(course);
                    }
                }
                // 如果课程时间在当前时间之后30分钟内，则状态为"进行中"
                else if (timeDiff <= thirtyMinutesInMillis) {
                    if (!"进行中".equals(course.getStatus())) {
                        course.setStatus("进行中");
                        courseDAO.update(course);
                    }
                }
                // 如果课程时间在当前时间之后30分钟以上，则状态为"未开始"
                else {
                    if (!"未开始".equals(course.getStatus())) {
                        course.setStatus("未开始");
                        courseDAO.update(course);
                    }
                }
            }
        }
    }
}