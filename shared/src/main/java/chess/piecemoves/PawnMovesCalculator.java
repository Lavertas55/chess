package chess.piecemoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator implements PieceMovesCalculator {
    private final int[][] attackDirections;
    private final int[][] directions;
    private final int promotionRow;
    private static final boolean moveToObstruction = false;

    public PawnMovesCalculator(ChessGame.TeamColor pieceColor, ChessPosition position) {
        int movementDirection = pieceColor == ChessGame.TeamColor.WHITE ? 1 : -1;
        int doubleMoveRow = pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7;
        promotionRow = pieceColor == ChessGame.TeamColor.WHITE ? 8 : 1;

        if (position.getRow() == doubleMoveRow) {
            directions = new int[][] { {movementDirection, 0}, {movementDirection * 2, 0} };
        }
        else {
            directions = new int[][]{{movementDirection, 0}};
        }

        attackDirections = new int[][] { {movementDirection, -1}, {movementDirection, 1} };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getDirections() {
        return directions;
    }

    /**
     * @return the possible attack directions
     */
    public int[][] getAttackDirections() {
        return attackDirections;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMoveToObstruction() {
        return moveToObstruction;
    }

    /**
     * A helper method that creates all the possible promotion moves for a pawn.
     *
     * @param start starting position of pawn
     * @param end ending position of pawn
     * @return a collection of possible chess moves with promotion options
     */
    public Collection<ChessMove> promotionHandler(ChessPosition start, ChessPosition end) {
        List<ChessMove> moves = new ArrayList<>();

        if (end.getRow() == promotionRow) {
            for (ChessPiece.PieceType pieceType : ChessPiece.PieceType.values()) {
                if (pieceType != ChessPiece.PieceType.KING && pieceType != ChessPiece.PieceType.PAWN) {
                    moves.add(new ChessMove(start, end, pieceType));
                }
            }
        }
        else {
            moves.add(new ChessMove(start, end, null));
        }

        return moves;
    }

    /**
     * Calculates the possible movements of a pawn given a ChessBoard and ChessPosition.
     *
     * @param board     the representation of the chess board
     * @param position  the position of the given chess piece
     * @return A collection of possible chessMoves
     */
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        List<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor pieceColor = piece.getTeamColor();
        int[][] directions = getDirections();

        for (int[] direction : directions) {
            int row = position.getRow() + direction[0];
            int col = position.getColumn() + direction[1];

            ChessPosition destination = new ChessPosition(row, col);

            if (board.inBounds(destination)) {
                ChessPiece destinationObj = board.getPiece(new ChessPosition(row, col));

                if (destinationObj == null) {
                    moves.addAll(promotionHandler(position, new ChessPosition(row, col)));
                }
                else {
                    break;
                }
            }
        }

        int[][] attackDirections = getAttackDirections();

        for (int[] attackDirection : attackDirections) {
            int row = position.getRow() + attackDirection[0];
            int col = position.getColumn() + attackDirection[1];

            ChessPosition destination = new ChessPosition(row, col);

            if (board.inBounds(destination)) {
                ChessPiece destinationObj = board.getPiece(new ChessPosition(row, col));

                if (destinationObj != null && destinationObj.getTeamColor() != pieceColor) {
                    moves.addAll(promotionHandler(position, new ChessPosition(row, col)));
                }
            }
        }

        return moves;
    }
}
