<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.sql.*" %>
<html>
<head>
    <title>数据库测试</title>
</head>
<body>
    <h1>数据库连接测试</h1>
    
    <% 
        String url = "jdbc:mysql://localhost:3306/stu_manage?serverTimezone=GMT%2B8&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false";
        String user = "root";
        String pass = "123456";
        
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            conn = DriverManager.getConnection(url, user, pass);
            out.println("<p>数据库连接成功！</p>");
            
            // 测试客户表
            out.println("<h2>客户表数据：</h2>");
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT id, name FROM tb_customer");
            
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>姓名</th></tr>");
            
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("</tr>");
            }
            
            if (!hasData) {
                out.println("<tr><td colspan='2'>客户表中没有数据</td></tr>");
            }
            
            out.println("</table>");
            rs.close();
            
            // 测试教练表
            out.println("<h2>教练表数据：</h2>");
            rs = stmt.executeQuery("SELECT id, name FROM tb_coach");
            
            out.println("<table border='1'>");
            out.println("<tr><th>ID</th><th>姓名</th></tr>");
            
            hasData = false;
            while (rs.next()) {
                hasData = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("</tr>");
            }
            
            if (!hasData) {
                out.println("<tr><td colspan='2'>教练表中没有数据</td></tr>");
            }
            
            out.println("</table>");
            
        } catch (Exception e) {
            out.println("<p>错误：" + e.getMessage() + "</p>");
            e.printStackTrace(new java.io.PrintWriter(out));
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    %>
</body>
</html>