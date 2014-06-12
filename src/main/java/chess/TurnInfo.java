package chess;

public class TurnInfo {
    private String  log;
    private boolean isCheck;

    public TurnInfo(String log, boolean isCheck) {
        this.log = log;
        this.isCheck = isCheck;
    }

    public String getLog() {
        return log;
    }

    public boolean isCheck() {
        return isCheck;
    }

}
