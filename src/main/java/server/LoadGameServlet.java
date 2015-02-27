package server;

import chess.ChessGame;
import db.DbHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class LoadGameServlet extends HttpServlet {

    private static void fwd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {req.getRequestDispatcher("/load.jsp").forward(req, resp);}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("/chess/login");
        } else {
            List<ChessGame.PackedChessGame> games = Scope.getSavedGames(req.getSession());
            String action = req.getParameter("action");
            if ("delete".equals(action)) {
                int idDel = Integer.parseInt(req.getParameter("id"));
                ListIterator<ChessGame.PackedChessGame> iterator = games.listIterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getId() == idDel) {
                        iterator.remove();
                        break;
                    }
                }
                DbHelper.deleteGame(idDel);
            } else if ("load".equals(action)) {
                int idLoad = Integer.parseInt(req.getParameter("id"));
                ChessGame.PackedChessGame pcg = null;
                for (ChessGame.PackedChessGame g : games) {
                    if (g.getId() == idLoad) {
                        pcg = g;
                        break;
                    }
                }
                if (pcg == null) {
                    fwd(req, resp);
                    return;
                }
                int friendId = user.getId() == pcg.getWhiteId() ? pcg.getBlackId() : pcg.getWhiteId();
                UserAccount friend = null;
                for (UserAccount f : Scope.getWaiters(req.getSession().getServletContext())) {
                    if (f.getId() == friendId) {
                        friend = f;
                    }
                }
                if (friend == null) {
                    req.setAttribute("error", "Friend doesn't want to play now.");
                } else {
                    ChessGame game = new ChessGame(pcg);
                    user.setState(UserAccount.State.PLAYING);
                    user.setGame(game);
                    user.setFriend(friend);
                    friend.setState(UserAccount.State.PLAYING);
                    friend.setGame(game);
                    friend.setFriend(user);
                    resp.sendRedirect("/chess/game");
                    return;
                }
            }
            if (games.isEmpty()) {
                req.setAttribute("is_empty", true);
            } else {
                req.getSession().setAttribute("games", games);
            }
            fwd(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect("/chess/login");
        } else {
            List<ChessGame.PackedChessGame> games = DbHelper.savedGames(user.getId());
            if (games.isEmpty()) {
                req.setAttribute("empty", true);
            } else {
                req.getSession().setAttribute("games", games);
            }
            fwd(req, resp);
        }
    }
}
