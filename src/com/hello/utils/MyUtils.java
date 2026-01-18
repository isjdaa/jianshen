package com.hello.utils;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 时间操作工具
 */
public class MyUtils {
    /**
     * 字符串转日期
     */
    public static Date strToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasPermission(HttpServletRequest req, HttpServletResponse resp, boolean isAjax, String... roles) throws ServletException, IOException {
        Object role = req.getSession().getAttribute("role");
        List<String> roleList = Arrays.asList(roles);
        if (role == null || !roleList.contains(role)) {
            if (!isAjax) {
                req.getRequestDispatcher("/login.jsp").forward(req, resp);
            } else {
                resp.getWriter().write(ApiResult.json(false, "没有权限"));
            }
            return false;
        }
        return true;
    }
}




