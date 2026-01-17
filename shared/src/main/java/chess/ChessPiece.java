package chess;

import chess.piecemoves.*;

import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        PieceType pieceType = piece.getPieceType();

        if (pieceType == PieceType.BISHOP) {
            BishopMovesCalculator moveCalculator = new BishopMovesCalculator();
            return moveCalculator.pieceMoves(board, myPosition);
        }
        else if (pieceType == PieceType.KING) {
            KingMovesCalculator moveCalculator = new KingMovesCalculator();
            return moveCalculator.pieceMoves(board, myPosition);
        }
        else if (pieceType == PieceType.KNIGHT) {
            KnightMovesCalculator moveCalculator = new KnightMovesCalculator();
            return moveCalculator.pieceMoves(board, myPosition);
        }
        else if (pieceType == PieceType.QUEEN) {
            QueenMovesCalculator moveCalculator = new QueenMovesCalculator();
            return moveCalculator.pieceMoves(board, myPosition);
        }
        else if (pieceType == PieceType.ROOK) {
            RookMovesCalculator moveCalculator = new RookMovesCalculator();
            return moveCalculator.pieceMoves(board, myPosition);
        }
        return List.of();
    }
}
