// Updated LoginServlet class
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userType = request.getParameter("usertype");
        if ("member".equals(userType)) {
            // Set session role 'member'
        }
    }
}