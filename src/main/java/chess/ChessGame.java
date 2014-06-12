package chess;

import java.util.*;

public class ChessGame {


    public static final  int   EMPTY_CELL = 20;
    public static final  int   WHITE      = 0;
    public static final  int   BLACK      = 1;
    public static final  int   PAWN       = 1;
    public static final  int   BISHOP     = 2;
    public static final  int   KNIGHT     = 3;
    public static final  int   ROOK       = 4;
    public static final  int   QUEEN      = 5;
    public static final  int   KING       = 6;
    private static final short BOARD_SIZE = 8;
    private final int[][] board;
    private final AttackArea aa = new AttackArea();
    private final StepArea   sa = new StepArea();
    private final MakesCheck mc = new MakesCheck();
    private int turnCount;
    private int[] players = new int[2];
    private TurnInfo lastTurnInfo;

    public ChessGame(int white, int black) {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        players[0] = white;
        players[1] = black;
        setUp(board);
        turnCount = 0;
    }

    public ChessGame(int[][] board, int turnCount, int[] players) {
        this.board = board;
        this.turnCount = turnCount;
        this.players = players;
    }

    public ChessGame(PackedChessGame packedChessGame) {
        turnCount = packedChessGame.turnCount;
        players[0] = packedChessGame.whiteId;
        players[1] = packedChessGame.blackId;
        board = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
        int it = 0;
        char[] cs = packedChessGame.fieldData.toCharArray();
        while (it < cs.length) {
            int fig = PackedChessGame.charToFig.get(cs[it++]);
            int i = Character.digit(cs[it++], 10);
            int j = Character.digit(cs[it++], 10);
            board[i][j] = fig;
        }
    }

    public static final class Cell {
        public static final char[]                  l            = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        public static final Map<Character, Integer> letter2index = new HashMap<Character, Integer>() {
            {
                put('a', 0);
                put('b', 1);
                put('c', 2);
                put('d', 3);
                put('e', 4);
                put('f', 5);
                put('g', 6);
                put('h', 7);
            }
        };
        final int i;
        final int j;

        public Cell(String pretty) {
            if (pretty.length() != 2) {
                throw new RuntimeException("Wrong cell: " + pretty);
            }

            char letter = pretty.charAt(0);
            char digit = pretty.charAt(1);
            i = 8 - Character.digit(digit, 10);
            j = letter2index.get(letter);
        }

        private Cell(int x, int y) {
            this.i = x;
            this.j = y;
        }

        public String toString() {
            return "[" + j + ", " + (8 - i) + ']';
        }

        public String toStringPretty() {
            return l[j] + "" + (8 - i);
        }

        public boolean wrong() {
            return 0 > i || i >= BOARD_SIZE || 0 > j || j >= BOARD_SIZE;
        }

        public boolean correct() {
            return 0 <= i && i < BOARD_SIZE && 0 <= j && j < BOARD_SIZE;
        }

        public Cell q() {
            return new Cell(i - 1, j - 1);
        }

        public Cell w() {
            return new Cell(i - 1, j);
        }

        public Cell e() {
            return new Cell(i - 1, j + 1);
        }

        public Cell a() {
            return new Cell(i, j - 1);
        }

        public Cell d() {
            return new Cell(i, j + 1);
        }

        public Cell z() {
            return new Cell(i + 1, j - 1);
        }

        public Cell x() {
            return new Cell(i + 1, j);
        }

        public Cell c() {
            return new Cell(i + 1, j + 1);
        }

        public Cell q(int i) {
            return new Cell(this.i - i, j - i);
        }

        public Cell w(int i) {
            return new Cell(this.i - i, j);
        }

        public Cell e(int i) {
            return new Cell(this.i - i, j + i);
        }

        public Cell a(int i) {
            return new Cell(this.i, j - i);
        }

        public Cell d(int i) {
            return new Cell(this.i, j + i);
        }

        public Cell z(int i) {
            return new Cell(this.i + i, j - i);
        }

        public Cell x(int i) {
            return new Cell(this.i + i, j);
        }

        public Cell c(int i) {
            return new Cell(this.i + i, j + i);
        }

        public boolean equals(Object o) {
            return o instanceof Cell && i == ((Cell) o).i && j == ((Cell) o).j;
        }
    }

    private static final class ToStringer {
        private static final char[] SS = {'_', 'p', 'b', 'k', 'r', 'q', 'g', 'X', 'X', 'X', 'X', 'P', 'B', 'K', 'R',
                'Q', 'G', 'X', 'X', 'X', '_'};
        private static final char[] S  = {'＿', '♙', '♗', '♘', '♖', '♕', '♔', 'X', 'X', 'X', 'X', '♟', '♝', '♞', '♜',
                '♛', '♚', 'X', 'X', 'X', '＿'};
        private static final char   I  = '|';

        public static String parseWithArea(int[][] board, List<Cell> area) {
            int[][] newBoard = new int[board.length][];
            for (int i = 0; i < board.length; i++) {
                newBoard[i] = Arrays.copyOf(board[i], board[i].length);
            }
            for (Cell c : area) {
                newBoard[c.i][c.j] = 10;
            }
            return parse(newBoard);
        }

        private static String parse(int[][] board) {
            return parse(board, true);
        }

        private static String parse(int[][] board, boolean simple) {
            StringBuilder sb = new StringBuilder();
            for (int[] line : board) {
                sb.append(I);
                for (int fig : line) {
                    sb.append((simple ? SS : S)[fig]);
                    sb.append(I);
                }
                sb.append('\n');
            }
            return sb.toString();
        }

        public static String toStringPretty(ChessGame game) {
            return parse(game.board, false);
        }

//        public static String showCell(ChessGame game, final Cell cell) {
//            return parseWithArea(game.board, new ArrayList<Cell>() {{add(cell);}});
//        }

        public static String showArea(ChessGame game, List<Cell> area) {
            removeWrongCells(area);
            return parseWithArea(game.board, area);
        }
    }

    public static final class PackedChessGame {
        private static final char[] SS = {'_', 'p', 'b', 'k', 'r', 'q', 'g', 'X', 'X', 'X', 'X', 'P', 'B', 'K', 'R',
                'Q', 'G', 'X', 'X', 'X', '_'};
        private final int    id;
        private final String fieldData;
        private final int    turnCount;
        private final int    whiteId;
        private final int    blackId;

        public PackedChessGame(ChessGame game) {
            id = -1;
            turnCount = game.getTurnCount();
            whiteId = game.getPlayers()[0];
            blackId = game.getPlayers()[1];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    int fig = game.board[i][j];
                    if (fig < EMPTY_CELL) {
                        sb.append(SS[fig]);
                        sb.append(i);
                        sb.append(j);
                    }
                }
            }
            fieldData = sb.toString();
        }

        public PackedChessGame(int id, String fieldData, int turnCount, int whiteId, int blackId) {
            this.id = id;
            this.fieldData = fieldData;
            this.turnCount = turnCount;
            this.whiteId = whiteId;
            this.blackId = blackId;
        }

        public boolean equals(Object o) {
            return o != null && o instanceof PackedChessGame && id == ((PackedChessGame) o).id;
        }

        public int getId() {
            return id;
        }

        public String getFieldData() {
            return fieldData;
        }

        public int getTurnCount() {
            return turnCount;
        }

        public int getWhiteId() {
            return whiteId;
        }

        public int getBlackId() {
            return blackId;
        }

        private static final Map<Character, Integer> charToFig = new HashMap<Character, Integer>() {
            {
                put('p', PAWN);
                put('b', BISHOP);
                put('k', KNIGHT);
                put('r', ROOK);
                put('q', QUEEN);
                put('g', KING);
                put('P', 10 + PAWN);
                put('B', 10 + BISHOP);
                put('K', 10 + KNIGHT);
                put('R', 10 + ROOK);
                put('Q', 10 + QUEEN);
                put('G', 10 + KING);
            }
        };


    }

    private final class MakesCheck {
        private boolean checkCell(Cell a, int kingColor) {
            int fig = getFigure(a);
            return fig % 10 == KING && fig / 10 == kingColor;
        }

        public boolean pawn(Cell c, int kingColor) {
            for (Cell a : aa.pawn(c)) {
                if (checkCell(a, kingColor)) {
                    return true;
                }
            }
            return false;
        }

        public boolean bishop(Cell c, int kingColor) {
            Cell bishRunner = c.e();
            while (isEmpty(bishRunner)) {
                bishRunner = bishRunner.e();
            }
            if (checkCell(bishRunner, kingColor)) { return true; }
            bishRunner = c.c();
            while (isEmpty(bishRunner)) {
                bishRunner = bishRunner.c();
            }
            if (checkCell(bishRunner, kingColor)) { return true; }
            bishRunner = c.z();
            while (isEmpty(bishRunner)) {
                bishRunner = bishRunner.z();
            }
            if (checkCell(bishRunner, kingColor)) { return true; }
            bishRunner = c.q();
            while (isEmpty(bishRunner)) {
                bishRunner = bishRunner.q();
            }
            return checkCell(bishRunner, kingColor);
        }

        public boolean rook(Cell cell, int kingColor) {
            Cell rookRunner = cell.d();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.d();
            }
            if (checkCell(rookRunner, kingColor)) { return true; }
            rookRunner = cell.x();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.x();
            }
            if (checkCell(rookRunner, kingColor)) { return true; }
            rookRunner = cell.a();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.a();
            }
            if (checkCell(rookRunner, kingColor)) { return true; }
            rookRunner = cell.w();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.w();
            }
            return checkCell(rookRunner, kingColor);
        }

        public boolean queen(Cell c, int kingColor) {
            return bishop(c, kingColor) || rook(c, kingColor);
        }

        public boolean knight(Cell c, int kingColor) {
            for (Cell a : aa.knight(c)) {
                if (checkCell(a, kingColor)) { return true; }
            }
            return false;
        }
    }

    @Deprecated
    private final class AttackArea {
        @Deprecated
        public List<Cell> bishop(Cell of) {
            List<Cell> list = new ArrayList<Cell>();
            Cell bishRunner = of.e();
            while (!bishRunner.wrong()) {
                list.add(bishRunner);
                bishRunner = bishRunner.e();
            }
            bishRunner = of.c();
            while (!bishRunner.wrong()) {
                list.add(bishRunner);
                bishRunner = bishRunner.c();
            }
            bishRunner = of.z();
            while (!bishRunner.wrong()) {
                list.add(bishRunner);
                bishRunner = bishRunner.z();
            }
            bishRunner = of.q();
            while (!bishRunner.wrong()) {
                list.add(bishRunner);
                bishRunner = bishRunner.q();
            }
            return list;
        }

        @Deprecated
        public List<Cell> rook(Cell cell) {
            List<Cell> list = new ArrayList<Cell>();
            int c = getFigureColor(getFigure(cell));
            Cell rookRunner = cell.d();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.d();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.x();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.x();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.a();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.a();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.w();
            while (isEmpty(rookRunner)) {
                rookRunner = rookRunner.w();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            return list;
        }

        @Deprecated
        public List<Cell> queen(Cell of) {
            List<Cell> bish = bishop(of);
            bish.addAll(rook(of));
            return bish;
        }

        @Deprecated
        public List<Cell> king(Cell of) {
            List<Cell> list = new ArrayList<Cell>();
            int c = getFigureColor(getFigure(of));
            Cell cell = of.q();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.w();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.e();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.a();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.d();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.z();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.x();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.c();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            removeWrongCells(list);
            return list;
        }

        @Deprecated
        public List<Cell> knight(Cell of) {
            List<Cell> list = new ArrayList<Cell>();
            int c = getFigureColor(getFigure(of));
            Cell cell = of.w(2).a();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.w(2).d();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }

            cell = of.d(2).w();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.d(2).x();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }

            cell = of.x(2).d();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.x(2).a();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }

            cell = of.a(2).w();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }
            cell = of.a(2).x();
            if (getFigureColor(getFigure(cell)) != c) { list.add(cell); }

            removeWrongCells(list);
            return list;
        }

        @Deprecated
        public List<Cell> pawn(Cell of) {
            List<Cell> list = new ArrayList<Cell>();
            int fig = getFigure(of);
            if (getFigureColor(fig) == WHITE) {
                Cell l = of.q();
                Cell r = of.e();
                if (l.correct() && getFigureColor(getFigure(l)) == BLACK) {
                    list.add(l);
                }
                if (r.correct() && getFigureColor(getFigure(r)) == BLACK) {
                    list.add(r);
                }
            } else {
                Cell l = of.z();
                Cell r = of.c();
                if (l.correct() && getFigureColor(getFigure(l)) == WHITE) {
                    list.add(l);
                }
                if (r.correct() && getFigureColor(getFigure(r)) == WHITE) {
                    list.add(r);
                }
            }
            return list;
        }
    }

    private final class StepArea {
        public List<Cell> pawn(Cell cell) {
            List<Cell> list = aa.pawn(cell);
            int fig = getFigure(cell);
            boolean canGo = true;
            if (getFigureColor(fig) == WHITE) {
                Cell l = cell.q();
                Cell r = cell.e();
                if (l.correct() && getFigureColor(getFigure(l)) == BLACK) {
                    if (getFigure(l) % 10 == PAWN) {
                        canGo = false;
                    }
                    list.add(l);
                }
                if (r.correct() && getFigureColor(getFigure(r)) == BLACK) {
                    if (getFigure(r) % 10 == PAWN) {
                        canGo = false;
                    }
                    list.add(r);
                }
                if (canGo) {
                    list.add(cell.w());
                    if (cell.i == 6) {
                        list.add(cell.w(2));
                    }
                }
            } else {
                Cell l = cell.c();
                Cell r = cell.z();
                if (l.correct() && getFigureColor(getFigure(l)) == WHITE) {
                    if (getFigure(l) % 10 == PAWN) {
                        canGo = false;
                    }
                    list.add(l);
                }
                if (r.correct() && getFigureColor(getFigure(r)) == WHITE) {
                    if (getFigure(r) % 10 == PAWN) {
                        canGo = false;
                    }
                    list.add(r);
                }
                if (canGo) {
                    list.add(cell.x());
                    if (cell.i == 1) {
                        list.add(cell.x(2));
                    }
                }
            }
            return list;
        }

        public List<Cell> bishop(Cell cell) {
            List<Cell> list = new ArrayList<Cell>();
            int c = getFigureColor(getFigure(cell));
            Cell bishRunner = cell.e();
            while (isEmpty(bishRunner)) {
                list.add(bishRunner);
                bishRunner = bishRunner.e();
            }
            if (getFigureColor(getFigure(bishRunner)) != c) { list.add(bishRunner); }
            bishRunner = cell.c();
            while (isEmpty(bishRunner)) {
                list.add(bishRunner);
                bishRunner = bishRunner.c();
            }
            if (getFigureColor(getFigure(bishRunner)) != c) { list.add(bishRunner); }
            bishRunner = cell.z();
            while (isEmpty(bishRunner)) {
                list.add(bishRunner);
                bishRunner = bishRunner.z();
            }
            if (getFigureColor(getFigure(bishRunner)) != c) { list.add(bishRunner); }
            bishRunner = cell.q();
            while (isEmpty(bishRunner)) {
                list.add(bishRunner);
                bishRunner = bishRunner.q();
            }
            if (getFigureColor(getFigure(bishRunner)) != c) { list.add(bishRunner); }
            return list;
        }

        public List<Cell> knight(Cell cell) {return aa.knight(cell);}

        public List<Cell> rook(Cell cell) {
            List<Cell> list = new ArrayList<Cell>();
            int c = getFigureColor(getFigure(cell));
            Cell rookRunner = cell.d();
            while (isEmpty(rookRunner)) {
                list.add(rookRunner);
                rookRunner = rookRunner.d();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.x();
            while (isEmpty(rookRunner)) {
                list.add(rookRunner);
                rookRunner = rookRunner.x();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.a();
            while (isEmpty(rookRunner)) {
                list.add(rookRunner);
                rookRunner = rookRunner.a();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            rookRunner = cell.w();
            while (isEmpty(rookRunner)) {
                list.add(rookRunner);
                rookRunner = rookRunner.w();
            }
            if (getFigureColor(getFigure(rookRunner)) != c) {
                list.add(rookRunner);
            }
            return list;
        }

        public List<Cell> queen(Cell cell) {
            List<Cell> bish = bishop(cell);
            bish.addAll(rook(cell));
            return bish;
        }

        public List<Cell> king(Cell cell) {return aa.king(cell);}
    }

    private static void setUp(int[][] board) {
        board[0][0] = ROOK + 10;
        board[0][1] = KNIGHT + 10;
        board[0][2] = BISHOP + 10;
        board[0][3] = QUEEN + 10;
        board[0][4] = KING + 10;
        board[0][5] = BISHOP + 10;
        board[0][6] = KNIGHT + 10;
        board[0][7] = ROOK + 10;
        final int BLACK_PAWN = PAWN + 10;
        for (int j = 0; j < BOARD_SIZE; j++) {
            board[1][j] = BLACK_PAWN;
        }
        for (int j = 0; j < BOARD_SIZE; j++) {
            board[6][j] = PAWN;
        }
        board[7][0] = ROOK;
        board[7][1] = KNIGHT;
        board[7][2] = BISHOP;
        board[7][3] = QUEEN;
        board[7][4] = KING;
        board[7][5] = BISHOP;
        board[7][6] = KNIGHT;
        board[7][7] = ROOK;
        for (int i = 2; i < 6; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = EMPTY_CELL;
            }
        }
    }

    private static void removeWrongCells(List<Cell> list) {
        ListIterator<Cell> iterator = list.listIterator();
        while (iterator.hasNext()) {
            Cell c = iterator.next();
            if (c.wrong()) { iterator.remove(); }
        }
    }

    public static void main(String[] args) {
        ChessGame game = new ChessGame(9, 1);
        PackedChessGame pcg = new PackedChessGame(game);
        ChessGame game2 = new ChessGame(pcg);
        System.out.println(game.equals(game2));
        System.out.println(ToStringer.toStringPretty(game2));
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof ChessGame) {
            ChessGame g = (ChessGame) o;
            if (g.turnCount != turnCount) { return false; }
            if (g.players[0] != players[0]) {
                return false;
            }
            if (g.players[1] != players[1]) {
                return false;
            }
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (g.board[i][j] == board[i][j]) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public String getPrettyJspField() {
        return ToStringer.toStringPretty(this).replace("\n", "<br>");
    }

    private int getFigureColor(int fig) { return fig % 100 / 10; }

    private int getFigure(Cell cell) {
        return cell.correct() ? board[cell.i][cell.j] : EMPTY_CELL;
    }

    private List<Cell> getStepArea(Cell of) {
        int fig = getFigure(of);
        switch (fig % 10) {
            case PAWN:
                return sa.pawn(of);
            case KNIGHT:
                return sa.knight(of);
            case QUEEN:
                return sa.queen(of);
            case BISHOP:
                return sa.bishop(of);
            case ROOK:
                return sa.rook(of);
            case KING:
                return sa.king(of);
            default:
                return new ArrayList<Cell>();
        }
    }

    private List<Cell> getAttackArea(Cell of) {
        int fig = getFigure(of);
        switch (fig % 10) {
            case PAWN:
                return aa.pawn(of);
            case KNIGHT:
                return aa.knight(of);
            case QUEEN:
                return aa.queen(of);
            case BISHOP:
                return aa.bishop(of);
            case ROOK:
                return aa.rook(of);
            case KING:
                return aa.king(of);
            default:
                return new ArrayList<Cell>();
        }
    }

    void endOfTurn() {
        turnCount++;
    }

    public String toString() {
        return ToStringer.parse(board);
    }

    public void makeTurn(Cell from, Cell to) {
        String log;
        if (from.wrong()) {
            throw new RuntimeException("Wrong turn: Wrong departure cell: " + from.toString());
        }
        if (to.wrong()) {
            throw new RuntimeException("Wrong turn: Wrong arrival cell: " + to.toString());
        }
        if (isEmpty(from)) {
            throw new RuntimeException("Wrong turn: Departure cell is empty");
        }
        int fig = getFigure(from);
        if (getFigureColor(fig) != actor()) {
            throw new RuntimeException("Wrong turn: You can't act with enemy chessman");
        }
        if (!getStepArea(from).contains(to)) {
            throw new RuntimeException(
                    "Wrong turn: There's no path from " + from.toStringPretty() + " to " + to.toStringPretty());
        }
        if (isEmpty(to)) {
            putFigureAt(fig, to);
            putFigureAt(EMPTY_CELL, from);
            if (isCheck(true)) {
                putFigureAt(fig, from);
                putFigureAt(EMPTY_CELL, to);
                throw new RuntimeException("Wrong turn: You can't make check to yourself");
            }
            log = String.format("%s moved: %s-%s", ToStringer.S[fig], from.toStringPretty(), to.toStringPretty())
                        .trim();
        } else {
            int def = getFigure(to);
            if (similarColorFigures(from, to)) {
                throw new RuntimeException(
                        "Wrong turn: Similar color figures at cells " + from.toStringPretty() + " and " +
                                to.toStringPretty()
                );
            }
            putFigureAt(fig, to);
            putFigureAt(EMPTY_CELL, from);
            if (isCheck(true)) {
                putFigureAt(fig, from);
                putFigureAt(def, to);
                throw new RuntimeException("Wrong turn: You can't make check to yourself");
            }
            log = String.format("%s ate %s %s:%s", ToStringer.S[fig], ToStringer.S[def], from.toStringPretty(),
                                to.toStringPretty());
        }
        lastTurnInfo = new TurnInfo(log, isCheck(false));
        endOfTurn();
    }

    private void putFigureAt(int fig, Cell at) {
        board[at.i][at.j] = fig;
    }

    private boolean isEmpty(Cell from) {
        return from.correct() && getFigure(from) % 10 == 0;
    }

    private boolean similarColorFigures(Cell c1, Cell c2) {
        return getFigure(c1) / 10 == getFigure(c2) / 10;
    }

    /**
     * TODO нужно, чтобы была не проверка по всем фигурам, а обратная, от короля:
     * пытаемся походить ходом фигуры типа Х. Если по полученной клетке стоит Х противника, то значит она и бьет
     * короля.
     *
     * @param self
     *
     * @return
     */
    private boolean isCheck(boolean self) {
        int attackerColor = actor();
        int defenderColor = ~actor();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                Cell c = new Cell(i, j);
                if (!isEmpty(c) && getFigure(c) / 10 == attackerColor) {
                    boolean tmpRes = false;
                    switch (getFigure(c) % 10) {
                        case PAWN:
                            tmpRes = mc.pawn(c, defenderColor);
                            break;
                        case KNIGHT:
                            tmpRes = mc.knight(c, defenderColor);
                            break;
                        case QUEEN:
                            tmpRes = mc.queen(c, defenderColor);
                            break;
                        case BISHOP:
                            tmpRes = mc.bishop(c, defenderColor);
                            break;
                        case ROOK:
                            tmpRes = mc.rook(c, defenderColor);
                            break;
                    }
                    if (tmpRes) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public TurnInfo getLastTurnInfo() {
        return lastTurnInfo;
    }

    public int getActor() {
        return players[actor()];
    }

    public int[] getPlayers() {
        return players;
    }

    private int actor() {
        return turnCount % 2;
    }

    public String getTurnColor() {
        return actor() == WHITE ? "whites" : "blacks";
    }
}
