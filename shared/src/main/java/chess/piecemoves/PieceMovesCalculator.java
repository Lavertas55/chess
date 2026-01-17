package chess.piecemoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface PieceMovesCalculator {
    /**
     * @return the possible directions the piece can move
     */
    int[][] getDirections();

    /**
     * @return if the piece will continue in a direction until stopped
     */
    boolean getMoveToObstruction();

    /**
     * Calculates the possible movements of a piece given a ChessBoard and ChessPosition.
     * Internally relies on getDirections and getMoveToObstruction defined in implementations.
     *
     * @param board     the representation of the chess board
     * @param position  the position of the given chess piece
     * @return A collection of possible chessMoves
     */
    default Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessMove> moves = new ArrayList<>();

        for (int[] direction : getDirections()) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];

            do {
                if (board.inBounds(row, col)) {
                    ChessPiece destination = board.getPiece(new ChessPosition(row, col));

                    if (destination == null) {
                        moves.add(new ChessMove(position, new ChessPosition(row, col), null));
                    } else if (destination.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(position, new ChessPosition(row, col), null));
                        break;
                    } else if (destination.getTeamColor() == piece.getTeamColor()) {
                        break;
                    }
                }

                row += direction[0];
                col += direction[1];
            } while (getMoveToObstruction() && board.inBounds(row, col));
        }

        return moves;
    }
}
