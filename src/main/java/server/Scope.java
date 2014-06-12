package server;

import chess.ChessGame;
import db.DbHelper;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

public class Scope {
    public static List<UserAccount> getWaiters(ServletContext context) {
        List<UserAccount> list = (List<UserAccount>) context.getAttribute("waiters");
        if (list == null) {
            list = new ArrayList<UserAccount>();
            context.setAttribute("waiters", list);
        }
        return list;
    }

    public static List<ChessGame.PackedChessGame> getSavedGames(HttpSession session) {
        List<ChessGame.PackedChessGame> list = (List<ChessGame.PackedChessGame>) session.getAttribute("games");
        if (list == null) {
            list = DbHelper.savedGames(((UserAccount) session.getAttribute("user")).getId());
            session.setAttribute("games", list);
        }
        return list;
    }
}
