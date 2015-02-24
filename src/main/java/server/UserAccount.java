package server;

import chess.ChessGame;
import db.DbHelper;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAccount {
    public enum State {
        PLAYING,
        SEARCHING,
        NOT_PLAYING,
    }

    private static final String SALT = "my name is Alice";

    private final String            login;
    private final long              passwordHash;
    private final List<UserAccount> playOffers;
    private       int               id;
    private       State             state;
    private       ChessGame         game;

    public UserAccount(String login, String password, boolean register) throws LoginException {
        this.login = login;
        this.passwordHash = hash(login, password);
        if (register) {
            id = DbHelper.tryRegister(login, passwordHash);
            if (id == -1) {
                throw new LoginException("Duplicate login");
            }
        } else {
            id = DbHelper.tryLogin(login, passwordHash);
            if (id == -1) {
                throw new LoginException("Wrong login or password");
            }
        }
        state = State.NOT_PLAYING;
        playOffers = new ArrayList<UserAccount>();
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof UserAccount) {
            UserAccount user = (UserAccount) o;
            return user.id == id;
        } else { return false; }

    }

    public boolean addOffer(UserAccount friend) {
        boolean connected = false;
        if (friend.getOffers().contains(this)) {
            state = State.PLAYING;
            friend.setState(State.PLAYING);

            playOffers.clear();

            friend.setFriend(this);
            connected = true;
        }
        playOffers.add(friend);
        return connected;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public int getId() {

        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<UserAccount> getOffers() {
        return playOffers;
    }

    public String getOffersString() {
        return playOffers.toString();
    }

    public int getOffersAmount() {
        return playOffers.size();
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getStateInt() {
        return state.ordinal();
    }

    public UserAccount getOpponent() {
        if (state == State.PLAYING) {
            return playOffers.get(0);
        } else { throw new IllegalStateException("Player has not chosen an opponent"); }
    }

    /**
     * Default djb2 hash alghoritm
     *
     * @param pass input string
     *
     * @return hash of input string
     */
    private static long hash(String login, String pass) {
        long hash = 5381;
        char[] chars = SALT.concat(pass).concat(login.toLowerCase(Locale.ENGLISH)).toCharArray();
        for (char c : chars) {
            hash = ((hash << 5) + hash) + c; //hash * 33 + c;
        }
        //to avoid similarity of hashes of same-length login+pass strings
        char last = login.charAt(login.length() - 1);
        int index = last % login.length();
        char pivot = login.charAt(index);
        hash <<= (pivot % 5);
        return hash;
    }

    public void setFriend(UserAccount friend) {
        playOffers.clear();
        playOffers.add(friend);
    }

    public void exit() {
        playOffers.clear();
        state = State.NOT_PLAYING;
    }

    public static void main(String[] args) {
        System.out.println(hash("ad", "qwed"));
        System.out.println(hash("йц", "ййцк"));
    }
}
