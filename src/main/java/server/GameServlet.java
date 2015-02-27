package server;

import chess.ChessGame;
import db.DbHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

public class GameServlet extends HttpServlet {

    private final Object gameCreationLocker = new Object();

    private static void fwd(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {req.getRequestDispatcher("/game.jsp").forward(req, resp);}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) (req.getSession().getAttribute("user"));
        if (user == null) {
            resp.sendRedirect("/chess/login");
        } else {
            if (user.getState() != UserAccount.State.PLAYING) {
                resp.sendRedirect("/chess/start");
                return;
            }
            ChessGame game = user.getGame();
            if (game == null) {
                UserAccount opponent = user.getOpponent();
                if (opponent.getGame() == null) {
                    int dice = new Random().nextInt() % 2;
                    game = dice ==
                            0 ? new ChessGame(user.getId(), opponent.getId()) : new ChessGame(opponent.getId(),
                                                                                              user.getId());
                    opponent.setGame(game);
                }
                user.setGame(opponent.getGame());
            }
            user.setState(UserAccount.State.PLAYING);
            fwd(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserAccount user = (UserAccount) (req.getSession().getAttribute("user"));
        if (user == null) {
            resp.sendRedirect("/chess/login");
        } else {
            if (user.getState() != UserAccount.State.PLAYING) {
                resp.sendRedirect("/chess/start");
                return;
            }
            String action = req.getParameter("action");
            if ("go".equals(action)) {
                String from = req.getParameter("from");
                String to = req.getParameter("to");
                try {
                    user.getGame().makeTurn(new ChessGame.Cell(from), new ChessGame.Cell(to));
                } catch (RuntimeException e) {
                    req.setAttribute("error", e.getMessage());
                }
            } else if ("save".equals(action)) {
                req.setAttribute("saved", DbHelper.saveGame(new ChessGame.PackedChessGame(user.getGame())));
            } else if ("exit".equals(action)) {
                user.getOpponent().exit();
                user.exit();
                resp.sendRedirect("/chess/start");
                return;
            }
            fwd(req, resp);
        }
    }

    private void addErrorString(HttpServletRequest req, String error) {
        req.setAttribute("error", error.concat("\n").concat((String) req.getAttribute("error")));
    }

}