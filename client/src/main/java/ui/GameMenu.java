package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.ServerFacade;
import client.State;
import client.UIEngine;

import java.io.PrintStream;
import java.util.Optional;

import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_WHITE;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public abstract class GameMenu extends UIMenu {
    private static final int BOARD_SIZE_IN_CELLS = 10;
    private static final int CELL_SIZE = 3;
    private static final String EMPTY = " ";

    final String authToken;
    final ChessGame game;
    final ChessGame.TeamColor teamColor;

    public GameMenu(
            UIEngine engine,
            ServerFacade serverFacade,
            String authToken,
            ChessGame game,
            ChessGame.TeamColor teamColor
    ) {
        super(engine, serverFacade);
        this.authToken = authToken;
        this.game = game;
        this.teamColor = teamColor;
    }

    @Override
    Optional<State> run() {
        drawBoard(System.out, game.getBoard(), teamColor);

        return super.run();
    }

    State exit() {
        notify("Exiting...\n");

        return State.SIGNED_IN;
    }

    void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor teamColor) {
        drawRankHeaders(out, teamColor);
        drawChessRows(out, teamColor, board);
        drawRankHeaders(out, teamColor);
    }

    private void drawRankHeaders(PrintStream out, ChessGame.TeamColor teamColor) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);

        String[] ranks = { "a", "b", "c", "d", "e", "f", "g", "h" };
        int boardColumn;
        int step;

        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            boardColumn = 0;
            step = 1;
        }
        else {
            boardColumn = 9;
            step = -1;
        }

        while (boardColumn < BOARD_SIZE_IN_CELLS && boardColumn >= 0) {
            if (boardColumn > 0 && boardColumn < 9) {
                drawCell(out, ranks[boardColumn - 1]);
            }
            else {
                out.print(EMPTY.repeat(CELL_SIZE));
            }

            boardColumn += step;
        }
        setBlack(out);
        out.println();
    }

    private void drawCell(PrintStream out, String text) {
        int prefixLength = CELL_SIZE / 2;
        int suffixLength = CELL_SIZE - prefixLength - 1;

        out.print(EMPTY.repeat(prefixLength));
        out.print(text);
        out.print(EMPTY.repeat(suffixLength));
    }

    private void drawChessRows(PrintStream out, ChessGame.TeamColor teamColor, ChessBoard board) {
        String rowStartColor = SET_BG_COLOR_WHITE;

        int boardRow;
        int step;
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            boardRow = 8;
            step = -1;
        }
        else {
            boardRow = 1;
            step = 1;
        }

        while (boardRow < BOARD_SIZE_IN_CELLS - 1 && boardRow > 0) {
            drawRow(out, boardRow, rowStartColor, board);
            rowStartColor = rowStartColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;

            boardRow += step;
        }
    }

    private void drawRow(PrintStream out, int rowNum, String startColor, ChessBoard board) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        drawCell(out, String.valueOf(rowNum));

        String cellColor = startColor;
        for (int boardColumn = 1; boardColumn < BOARD_SIZE_IN_CELLS - 1; boardColumn++) {
            ChessPiece piece = board.getPiece(new ChessPosition(rowNum, boardColumn));
            String pieceString = piece == null ? EMPTY : piece.toString().toUpperCase();

            if (piece != null && piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                pieceString = SET_TEXT_COLOR_RED + pieceString;
            }
            else if (piece != null && piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                pieceString = SET_TEXT_COLOR_BLUE + pieceString;
            }

            out.print(cellColor);
            drawCell(out, pieceString);

            cellColor = cellColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        }

        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        drawCell(out, String.valueOf(rowNum));

        setBlack(out);
        out.println();
    }

    private void setBlack(PrintStream out) {
        out.print(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }
}
