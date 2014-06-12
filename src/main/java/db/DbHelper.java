package db;

import chess.ChessGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbHelper {

    private static final String DB_CREATE_REG_DATA = "CREATE TABLE IF NOT EXISTS " +
            "reg_data" +
            "(" +
            "id INT(11) NOT NULL AUTO_INCREMENT," +
            "login VARCHAR(255) UNIQUE NOT NULL," +
            "pass BIGINT NOT NULL," +
            "PRIMARY KEY(id)" +
            ");";

    private static final String JDBC_DRIVER              = "com.mysql.jdbc.Driver";
    private static final String JDBC_CONNECTION          = "jdbc:mysql://127.0.0.1:3306/chess_online";
    private static final String DB_CREATE_SAVED_GAMES    = "CREATE TABLE IF NOT EXISTS " +
            "saved_games" +
            "(" +
            "id int NOT NULL AUTO_INCREMENT," +
            "field_data varchar(255) NOT NULL," +
            "turn_count int NOT NULL," +
            "white_id int NOT NULL," +
            "black_id int NOT NULL," +
            "PRIMARY KEY(id)" +
            ");";
    private static final String DB_CREATE_USERS_TO_GAMES = "CREATE TABLE IF NOT EXISTS " +
            "users2games" +
            "(" +
            "user_id int NOT NULL," +
            "game_id int NOT NULL" +
            ");";
    private static Connection connection;

    static {
        init();
    }

    public static void init() {
        if (connection != null) {
            return;
        }
        try {
            DriverManager.registerDriver((Driver) Class.forName(JDBC_DRIVER).newInstance());
        } catch (Exception e) { System.out.println("Unable to load the JDBC driver class"); }
        try {
            connection = DriverManager.getConnection(JDBC_CONNECTION, "root", "");
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Database does not exist");
        }
        try {
            connection.prepareStatement(DB_CREATE_REG_DATA).execute();
            connection.prepareStatement(DB_CREATE_SAVED_GAMES).execute();
            connection.prepareStatement(DB_CREATE_USERS_TO_GAMES).execute();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static int tryRegister(String login, long pass) {
        final String statement = "INSERT INTO reg_data (login,pass) VALUES (\"%s\", %s);";
        try {
            connection.prepareStatement(String.format(statement, login, pass)).execute();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
        return getLastInsertId();
    }

    public static int tryLogin(String login, long pass) {
        final String tryLogin = "SELECT id FROM reg_data WHERE login=\"%s\" AND pass=%s;";
        try {
            ResultSet rs = connection.prepareStatement(String.format(tryLogin, login, pass)).executeQuery();
            rs.first();
            return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    public static boolean saveGame(ChessGame.PackedChessGame pcg) {
        final String save = "INSERT INTO saved_games (field_data, turn_count, white_id, " +
                "black_id) VALUES (\"%s\", %s, %s, %s);";
        final String link = "INSERT INTO users2games (user_id, game_id) VALUES (%s, %s)";
        try {
            connection.prepareStatement(String.format(save, pcg.getFieldData(), pcg.getTurnCount(), pcg.getWhiteId(),
                                                      pcg
                                                              .getBlackId()
                                                     )).execute();
            int gameId = getLastInsertId();
            connection.prepareStatement(String.format(link, pcg.getWhiteId(), gameId)).execute();
            connection.prepareStatement(String.format(link, pcg.getBlackId(), gameId)).execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteGame(int id) {
        final String userIds = "SELECT white_id, black_id FROM saved_games WHERE id=%s";
        final String deleteGame = "DELETE FROM saved_games WHERE id=%s;";
        final String deleteLink = "DELETE FROM users2games WHERE user_id=%s AND game_id=%s";
        try {
            ResultSet rs = connection.prepareStatement(String.format(userIds, id)).executeQuery();
            rs.first();
            int white = rs.getInt(1);
            int black = rs.getInt(2);

            try {
                connection.setAutoCommit(false);
                connection.prepareStatement(String.format(deleteGame, id)).execute();
                connection.prepareStatement(String.format(deleteLink, white, id)).execute();
                connection.prepareStatement(String.format(deleteLink, black, id)).execute();
                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                e.printStackTrace();
                return false;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getLastInsertId() {
        final String last = "SELECT LAST_INSERT_ID();";
        try {
            ResultSet rs = connection.prepareStatement(last).executeQuery();
            rs.first();
            return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static void main(String[] args) {
        System.out.println(tryRegister("Login", 5));
    }

    public static List<ChessGame.PackedChessGame> savedGames(int id) {
        final String join = "SELECT T1.id, T1.field_data, T1.turn_count, T1.white_id, T1.black_id " +
                "FROM (SELECT * FROM saved_games) AS T1 " +
                "JOIN (SELECT game_id FROM users2games WHERE user_id=%s) AS T2 " +
                "ON T1.id=T2.game_id;";
        try {
            ResultSet rs = connection.prepareStatement(String.format(join, id)).executeQuery();
            List<ChessGame.PackedChessGame> list = new ArrayList<ChessGame.PackedChessGame>();
            rs.beforeFirst();
            while (rs.next()) {
                int saveId = rs.getInt(1);
                String s = rs.getString(2);
                int t = rs.getInt(3);
                int w = rs.getInt(4);
                int b = rs.getInt(5);
                list.add(new ChessGame.PackedChessGame(saveId, s, t, w, b));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}