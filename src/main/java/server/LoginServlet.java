package server;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    public static final String JASPER = "/auth/login.jsp";

    private static void fwd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {req.getRequestDispatcher(JASPER).forward(req, resp);}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) req.getSession().getAttribute("user");
        if (user != null) {
            resp.sendRedirect("/chessonline/start");
            return;
        }
        fwd(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("signup".equals(action)) {
            resp.sendRedirect("/chessonline/register");
        } else if ("signin".equals(action)) {
            String login = req.getParameter("login");
            String password = req.getParameter("password");
            try {
                UserAccount user = new UserAccount(login, password, false);
                req.getSession().setAttribute("user", user);
                resp.sendRedirect("/chessonline/start");
            } catch (LoginException e) {
                req.setAttribute("fail", true);
                fwd(req, resp);
            }
        } else if ("logout".equals(action)) {
            req.getSession().invalidate();
            fwd(req, resp);
        }
    }
}
