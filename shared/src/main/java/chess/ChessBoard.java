package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 1; row <= board.length; row++) {
            ChessGame.TeamColor pieceColor;
            ChessPiece.PieceType pieceType = ChessPiece.PieceType.PAWN;

            if (row <= 2) {
                pieceColor = ChessGame.TeamColor.WHITE;
            }
            else if (row >= 7) {
                pieceColor = ChessGame.TeamColor.BLACK;
            }
            else {
                pieceColor = null;
            }

            for (int col = 1; col <= board[row - 1].length; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);

                if (row == 1 || row == 8) {
                    pieceType = switch (col) {
                        case 1, 8 -> ChessPiece.PieceType.ROOK;
                        case 2, 7 -> ChessPiece.PieceType.KNIGHT;
                        case 3, 6 -> ChessPiece.PieceType.BISHOP;
                        case 4 -> ChessPiece.PieceType.QUEEN;
                        case 5 -> ChessPiece.PieceType.KING;
                        default -> null;
                    };
                }

                if (pieceType != null && pieceColor != null) {
                    this.addPiece(currentPosition, new ChessPiece(pieceColor, pieceType));
                } else {
                    this.addPiece(currentPosition, null);
                }
            }
        }
    }


    /**
     * Determines if a given coordinates is in bounds
     *
     * @param row The row number to check
     * @param col The column number to check
     * @return True if the position is in bounds, False otherwise
     */
    public boolean inBounds(int row, int col) {
        return row > 0 && row <= board.length && col > 0 && col <= board[row-1].length;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (ChessPiece[] row : board) {
            StringBuilder rowString = new StringBuilder();

            for (ChessPiece piece : row) {
                if (piece == null) {
                    rowString.append(" |");
                }
                else {
                    rowString.append(String.format("%s|", piece));
                }
            }

            result.insert(0, String.format("%s\n", rowString.substring(0, rowString.length() - 1)));
        }

        return result.toString();
    }
}