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
        if ("register".equals(action)) {
            String login = req.getParameter("login");
            String password = req.getParameter("password");
            boolean fail = false;
            if (password.length() < 3) {
                fail = true;
                req.setAttribute("shortPassword", true);
            }
            if (login.length() == 0) {
                fail = true;
                req.setAttribute("emptyLogin", true);
            }
            if (login.contains(" ")) {
                fail = true;
                req.setAttribute("spacesInLogin", true);
            }
            if (!fail) {
                try {
                    UserAccount newAccount = new UserAccount(login, password, true);
                    req.getSession().setAttribute("user", newAccount);
                } catch (LoginException e) {
                    fail = true;
                    req.setAttribute("duplicateLogin", true);
                }
            }
            req.setAttribute("fail", fail);
        } //else it's "signup" from login.jsp
        req.getRequestDispatcher(JASPER).forward(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) req.getSession().getAttribute("user");
        if (user != null) {
            resp.sendRedirect("/start");
            return;
        }
        req.getRequestDispatcher(JASPER).forward(req, resp);
    }
}
