package com.hello.servlet;

import com.hello.dao.CourseDAO;
import com.hello.entity.Coach;
import com.hello.entity.Course;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebServlet({"/schedule"})
public class ScheduleServlet extends HttpServlet {
    private CourseDAO courseDAO = new CourseDAO();
    private CoachService coachService = new CoachService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String action = req.getParameter("action");

        // 检查权限，管理员和教练都可以访问排课功能
        boolean hasPermission = MyUtils.hasPermission(req, resp, false, "admin", "coach");
        if (!hasPermission) {
            return;
        }

        if (action == null || action.isEmpty()) {
            // 默认显示课程列表
            handleScheduleList(req, resp);
        } else if (action.equals("view")) {
            // 查看课程列表
            handleScheduleList(req, resp);
        } else if (action.equals("add")) {
            // 添加课程页面
            handleAddSchedule(req, resp);
        } else if (action.equals("edit")) {
            // 编辑课程页面
            handleEditSchedule(req, resp);
        } else if (action.equals("delete")) {
            // 删除课程
            handleDeleteSchedule(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        // 检查权限，管理员和教练都可以操作排课功能
        boolean hasPermission = MyUtils.hasPermission(req, resp, true, "admin", "coach");
        if (!hasPermission) {
            out.print(ApiResult.json(false, "没有权限操作排课"));
            out.flush();
            out.close();
            return;
        }

        String action = req.getParameter("action");
        ApiResult apiResult = new ApiResult();

        if (action.equals("add")) {
            apiResult = handleAddSchedulePost(req);
        } else if (action.equals("update")) {
            apiResult = handleUpdateSchedulePost(req);
        }

        out.print(apiResult.toJson());
        out.flush();
        out.close();
    }

    // 处理课程列表页面
    private void handleScheduleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        // 获取当前用户角色
        String role = (String) req.getSession().getAttribute("role");
        PagerVO<Course> coursePager;

        if (role.equals("admin")) {
            // 管理员查看所有课程
            coursePager = courseDAO.findAll(currentPage, pageSize);
        } else {
            // 教练只能查看自己的课程
            Coach coach = (Coach) req.getSession().getAttribute("user");
            coursePager = courseDAO.findByCoachId(currentPage, pageSize, coach.getId());
        }

        // 初始化分页属性
        coursePager.init();

        // 自动更新课程状态
        updateCourseStatuses(coursePager.getList());

        // 获取所有教练信息，用于在列表中显示教练姓名
        List<Coach> allCoaches = coachService.page(1, 100, null, null, null, null).getList();
        req.setAttribute("allCoaches", allCoaches);

        req.setAttribute("coursePager", coursePager);
        req.setAttribute("courses", coursePager.getList());
        req.getRequestDispatcher("/WEB-INF/view/schedule-form.jsp").forward(req, resp);
    }

    // 处理添加课程页面
    private void handleAddSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 获取所有教练
        List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
        req.setAttribute("coaches", coaches);
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/WEB-INF/view/schedule-form.jsp").forward(req, resp);
    }

    // 处理编辑课程页面
    private void handleEditSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/schedule");
            return;
        }

        // 获取课程信息
        Course course = courseDAO.getById(id);
        if (course == null) {
            resp.sendRedirect(req.getContextPath() + "/schedule");
            return;
        }

        // 检查权限，教练只能编辑自己的课程
        String role = (String) req.getSession().getAttribute("role");
        if (role.equals("coach")) {
            Coach coach = (Coach) req.getSession().getAttribute("user");
            if (!course.getCoachId().equals(coach.getId())) {
                resp.sendRedirect(req.getContextPath() + "/schedule");
                return;
            }
        }

        // 获取所有教练
        List<Coach> coaches = coachService.page(1, 100, null, null, null, null).getList();
        req.setAttribute("coaches", coaches);
        req.setAttribute("isEdit", true);
        req.setAttribute("schedule", course);
        req.getRequestDispatcher("/WEB-INF/view/schedule-form.jsp").forward(req, resp);
    }

    // 处理删除课程
    private void handleDeleteSchedule(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=utf-8");
        PrintWriter out = resp.getWriter();

        String id = req.getParameter("id");
        if (id == null || id.isEmpty()) {
            out.print(ApiResult.json(false, "课程ID不能为空"));
            out.flush();
            out.close();
            return;
        }

        // 获取课程信息
        Course course = courseDAO.getById(id);
        if (course == null) {
            out.print(ApiResult.json(false, "课程不存在"));
            out.flush();
            out.close();
            return;
        }

        // 检查权限，教练只能删除自己的课程
        String role = (String) req.getSession().getAttribute("role");
        if (role.equals("coach")) {
            Coach coach = (Coach) req.getSession().getAttribute("user");
            if (!course.getCoachId().equals(coach.getId())) {
                out.print(ApiResult.json(false, "没有权限删除该课程"));
                out.flush();
                out.close();
                return;
            }
        }

        // 执行删除操作
        try {
            // 调用CourseDAO的delete方法删除课程
            int result = courseDAO.delete(id);
            if (result > 0) {
                out.print(ApiResult.json(true, "课程删除成功"));
            } else {
                out.print(ApiResult.json(false, "课程删除失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print(ApiResult.json(false, "删除课程失败：" + e.getMessage()));
        } finally {
            out.flush();
            out.close();
        }
    }

    // 处理添加课程的POST请求
    private ApiResult handleAddSchedulePost(HttpServletRequest req) {
        ApiResult apiResult = new ApiResult();

        try {
            // 获取请求参数
            String coachId = req.getParameter("coachId") != null ? req.getParameter("coachId").trim() : "";
            String courseName = req.getParameter("courseName") != null ? req.getParameter("courseName").trim() : "";
            String courseTimeStr = req.getParameter("courseTime") != null ? req.getParameter("courseTime").trim() : "";
            String durationStr = req.getParameter("duration") != null ? req.getParameter("duration").trim() : "";
            String maxStudentsStr = req.getParameter("maxStudents") != null ? req.getParameter("maxStudents").trim() : "";
            String description = req.getParameter("description") != null ? req.getParameter("description").trim() : "";

            // 参数校验
            if (coachId.isEmpty() || courseName.isEmpty() || courseTimeStr.isEmpty() || durationStr.isEmpty() || maxStudentsStr.isEmpty()) {
                return apiResult.error("请填写所有必填字段");
            }

            // 解析课程时间
            Date courseTime = parseDateTime(courseTimeStr);
            if (courseTime == null) {
                return apiResult.error("课程时间格式不正确");
            }

            // 检查课程时间是否早于当前时间
            if (courseTime.before(new Date())) {
                return apiResult.error("课程时间不能早于当前时间");
            }

            // 解析其他参数
            int duration = Integer.parseInt(durationStr);
            int maxStudents = Integer.parseInt(maxStudentsStr);

            // 检查参数有效性
            if (duration <= 0) {
                return apiResult.error("课程时长必须为正数");
            }
            if (maxStudents <= 0) {
                return apiResult.error("最大报名人数必须为正数");
            }

            // 创建课程对象
            Course course = new Course();
            course.setId(UUID.randomUUID().toString().replace("-", ""));
            course.setCoachId(coachId);
            course.setCourseName(courseName);
            course.setCourseTime(courseTime);
            course.setDuration(duration);
            course.setMaxStudents(maxStudents);
            course.setCurrentStudents(0);
            course.setStatus("未开始"); // 默认状态为未开始
            course.setDescription(description);

            // 保存课程
            int result = courseDAO.insert(course);
            if (result > 0) {
                apiResult.success("课程创建成功");
            } else {
                apiResult.error("课程创建失败，请稍后重试");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            apiResult.error("参数格式不正确：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("创建课程失败：" + e.getMessage());
        }

        return apiResult;
    }

    // 处理更新课程的POST请求
    private ApiResult handleUpdateSchedulePost(HttpServletRequest req) {
        ApiResult apiResult = new ApiResult();

        try {
            // 获取请求参数
            String id = req.getParameter("id") != null ? req.getParameter("id").trim() : "";
            String coachId = req.getParameter("coachId") != null ? req.getParameter("coachId").trim() : "";
            String courseName = req.getParameter("courseName") != null ? req.getParameter("courseName").trim() : "";
            String courseTimeStr = req.getParameter("courseTime") != null ? req.getParameter("courseTime").trim() : "";
            String durationStr = req.getParameter("duration") != null ? req.getParameter("duration").trim() : "";
            String maxStudentsStr = req.getParameter("maxStudents") != null ? req.getParameter("maxStudents").trim() : "";
            String description = req.getParameter("description") != null ? req.getParameter("description").trim() : "";

            // 参数校验
            if (id.isEmpty() || coachId.isEmpty() || courseName.isEmpty() || courseTimeStr.isEmpty() || durationStr.isEmpty() || maxStudentsStr.isEmpty()) {
                return apiResult.error("请填写所有必填字段");
            }

            // 检查课程是否存在
            Course existingCourse = courseDAO.getById(id);
            if (existingCourse == null) {
                return apiResult.error("课程不存在");
            }

            // 解析课程时间
            Date courseTime = parseDateTime(courseTimeStr);
            if (courseTime == null) {
                return apiResult.error("课程时间格式不正确");
            }

            // 检查课程时间是否早于当前时间
            if (courseTime.before(new Date())) {
                return apiResult.error("课程时间不能早于当前时间");
            }

            // 解析其他参数
            int duration = Integer.parseInt(durationStr);
            int maxStudents = Integer.parseInt(maxStudentsStr);

            // 检查参数有效性
            if (duration <= 0) {
                return apiResult.error("课程时长必须为正数");
            }
            if (maxStudents <= 0) {
                return apiResult.error("最大报名人数必须为正数");
            }
            if (maxStudents < existingCourse.getCurrentStudents()) {
                return apiResult.error("最大报名人数不能小于已报名人数");
            }

            // 更新课程对象
            existingCourse.setCoachId(coachId);
            existingCourse.setCourseName(courseName);
            existingCourse.setCourseTime(courseTime);
            existingCourse.setDuration(duration);
            existingCourse.setMaxStudents(maxStudents);
            existingCourse.setDescription(description);

            // 保存更新
            int result = courseDAO.update(existingCourse);
            if (result > 0) {
                apiResult.success("课程更新成功");
            } else {
                apiResult.error("课程更新失败，请稍后重试");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            apiResult.error("参数格式不正确：" + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            apiResult.error("更新课程失败：" + e.getMessage());
        }

        return apiResult;
    }

    // 解析日期时间字符串
    private Date parseDateTime(String dateTimeStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            return format.parse(dateTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
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