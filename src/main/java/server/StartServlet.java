package server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StartServlet extends HttpServlet {

    private static void fwd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {req.getRequestDispatcher("/start.jsp").forward(req, resp);}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) (req.getSession().getAttribute("user"));
        if (user == null) {
            resp.sendRedirect("/chess/login");
        } else if (user.getState() == UserAccount.State.PLAYING) {
            resp.sendRedirect("/chess/game");
        } else {
            if (user.getState() == UserAccount.State.SEARCHING) {
                resp.setIntHeader("Refresh", 5);
            }
            fwd(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        UserAccount currentUser = (UserAccount) (req.getSession().getAttribute("user"));
        if (currentUser == null) {
            resp.sendRedirect("/chess/login");
        } else if (currentUser.getState() == UserAccount.State.PLAYING) {
            resp.sendRedirect("/chess/game");
        } else if ("find".equals(action)) {
            currentUser.setState(UserAccount.State.SEARCHING);
            Scope.getWaiters(req.getSession().getServletContext()).add(currentUser);
            resp.setIntHeader("Refresh", 5);
            fwd(req, resp);
        } else if ("play".equals(action)) {
            resp.setIntHeader("Refresh", 5);
            int friendId = Integer.parseInt(req.getParameter("id"));
            UserAccount friend = null;
            for (UserAccount user : Scope.getWaiters(req.getSession().getServletContext())) {
                if (user.getId() == friendId) {
                    friend = user;
                }
            }
            if (friend != null) {
                if (friend.addOffer(currentUser)) {
                    resp.sendRedirect("/chess/game");
                    return;
                }
                req.setAttribute("friend", friend.getLogin().concat(".").concat(Integer.toString(friendId)));
            }
            fwd(req, resp);
        } else if ("load".equals(action)) {
            resp.sendRedirect("/chess/load");
        }
    }
}
