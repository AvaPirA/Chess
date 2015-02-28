package server;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegistrationServlet extends HttpServlet {

    public static final String JASPER = "/auth/register.jsp";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("REGISTER".equals(action)) {
            String login = req.getParameter("login");
            String password = req.getParameter("password");
            boolean fail = false;
            if (password.length() < 3 || login.length() == 0 || login.contains(" ")) {
                fail = true;
            }
            if (!fail) {
                try {
                    UserAccount newAccount = new UserAccount(login, password, true);
                    req.getSession().setAttribute("user", newAccount);
                    resp.getWriter().write("OK");
                } catch (LoginException e) {
                    resp.getWriter().write("FAIL_REG");
                }
            } else {
                resp.getWriter().write("FAIL_REQ");
            }
            resp.getWriter().flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) req.getSession().getAttribute("user");
        if (user != null) {
            resp.sendRedirect("/chess/start");
            return;
        }
        req.getRequestDispatcher(JASPER).forward(req, resp);
    }
}
