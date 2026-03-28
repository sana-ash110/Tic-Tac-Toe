package com.example.tictactoe;

/**
 * TicTacToe.java
 * Pure game-logic class — no Android dependencies.
 * Handles board state, move validation, win/draw detection.
 */
public class TicTacToe {

    public static final char PLAYER_X = 'X';
    public static final char PLAYER_O = 'O';
    public static final char EMPTY    = ' ';

    public enum GameState {
        ONGOING,
        X_WINS,
        O_WINS,
        DRAW
    }

    private final char[][] board = new char[3][3];
    private char currentPlayer;
    private GameState gameState;
    private int[] winningCells; // flat indices of the 3 winning cells (for highlight)

    // Scores persist across rounds
    private int scoreX;
    private int scoreO;
    private int scoreDraw;

    public TicTacToe() {
        scoreX = scoreO = scoreDraw = 0;
        resetBoard();
    }

    /** Clears the board for a new round without touching scores. */
    public void resetBoard() {
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                board[r][c] = EMPTY;
        currentPlayer = PLAYER_X;
        gameState     = GameState.ONGOING;
        winningCells  = null;
    }

    /** Resets scores AND board. */
    public void resetAll() {
        scoreX = scoreO = scoreDraw = 0;
        resetBoard();
    }

    /**
     * Attempts to play at (row, col).
     * @return true if the move was accepted, false if cell is occupied or game over.
     */
    public boolean makeMove(int row, int col) {
        if (gameState != GameState.ONGOING) return false;
        if (board[row][col] != EMPTY)       return false;

        board[row][col] = currentPlayer;
        gameState = evaluate();

        if (gameState == GameState.X_WINS) scoreX++;
        else if (gameState == GameState.O_WINS) scoreO++;
        else if (gameState == GameState.DRAW)   scoreDraw++;

        if (gameState == GameState.ONGOING) {
            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
        return true;
    }

    // ── Internals ─────────────────────────────────────────────────────────────

    private GameState evaluate() {
        // Rows
        for (int r = 0; r < 3; r++) {
            if (check(r,0, r,1, r,2)) return winnerState();
        }
        // Cols
        for (int c = 0; c < 3; c++) {
            if (check(0,c, 1,c, 2,c)) return winnerState();
        }
        // Diagonals
        if (check(0,0, 1,1, 2,2)) return winnerState();
        if (check(0,2, 1,1, 2,0)) return winnerState();

        // Draw?
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c] == EMPTY) return GameState.ONGOING;

        return GameState.DRAW;
    }

    private boolean check(int r1,int c1, int r2,int c2, int r3,int c3) {
        char v = board[r1][c1];
        if (v == EMPTY) return false;
        if (v == board[r2][c2] && v == board[r3][c3]) {
            winningCells = new int[]{r1*3+c1, r2*3+c2, r3*3+c3};
            return true;
        }
        return false;
    }

    private GameState winnerState() {
        return (currentPlayer == PLAYER_X) ? GameState.X_WINS : GameState.O_WINS;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public char getCellValue(int row, int col) { return board[row][col]; }
    public char getCurrentPlayer()             { return currentPlayer;   }
    public GameState getGameState()            { return gameState;       }
    public int   getScoreX()                   { return scoreX;          }
    public int   getScoreO()                   { return scoreO;          }
    public int   getScoreDraw()                { return scoreDraw;       }

    /** Returns flat indices (0-8) of the 3 winning cells, or null if no winner yet. */
    public int[] getWinningCells()             { return winningCells;    }

    public boolean isGameOver() {
        return gameState != GameState.ONGOING;
    }
}