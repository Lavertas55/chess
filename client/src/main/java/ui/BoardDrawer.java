package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class BoardDrawer {
    private static final int BOARD_SIZE_IN_CELLS = 10;
    private static final int CELL_SIZE = 3;
    private static final String EMPTY = " ";

    public void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor teamColor) {
        drawBoard(out, board, teamColor, null, new HashSet<>());
    }

    public void drawBoard(
            PrintStream out,
            ChessBoard board,
            ChessGame.TeamColor teamColor,
            ChessPosition start,
            Collection<ChessPosition> spacesToHighlight
    ) {
        System.out.println();
        drawRankHeaders(out, teamColor);
        drawChessRows(out, teamColor, board, start, spacesToHighlight);
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

    private void drawChessRows(
            PrintStream out,
            ChessGame.TeamColor teamColor,
            ChessBoard board,
            ChessPosition start,
            Collection<ChessPosition> spacesToHighlight
    ) {
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
            drawRow(out, boardRow, rowStartColor, board, teamColor, start, spacesToHighlight);
            rowStartColor = rowStartColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;

            boardRow += step;
        }
    }

    private void drawRow(
            PrintStream out,
            int rowNum,
            String startColor,
            ChessBoard board,
            ChessGame.TeamColor teamColor,
            ChessPosition start,
            Collection<ChessPosition> spacesToHighlight
    ) {
        out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        drawCell(out, String.valueOf(rowNum));

        int boardColumn;
        int step;
        if (teamColor.equals(ChessGame.TeamColor.WHITE)) {
            boardColumn = 1;
            step = 1;
        }
        else {
            boardColumn = 8;
            step = -1;
        }

        String cellColor = startColor;
        while (boardColumn < BOARD_SIZE_IN_CELLS - 1 && boardColumn > 0) {
            ChessPosition currentPosition = new ChessPosition(rowNum, boardColumn);

            ChessPiece piece = board.getPiece(currentPosition);
            String pieceString = piece == null ? EMPTY : piece.toString().toUpperCase();

            if (piece != null && piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)) {
                pieceString = SET_TEXT_COLOR_RED + pieceString;
            }
            else if (piece != null && piece.getTeamColor().equals(ChessGame.TeamColor.BLACK)) {
                pieceString = SET_TEXT_COLOR_BLUE + pieceString;
            }

            String oldColor = cellColor;
            if (spacesToHighlight.contains(currentPosition)) {

                cellColor = cellColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
            }

            if (currentPosition.equals(start)) {
                cellColor = SET_BG_COLOR_YELLOW;
            }

            out.print(cellColor);
            drawCell(out, pieceString);

            cellColor = oldColor;
            cellColor = cellColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;

            boardColumn += step;
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
