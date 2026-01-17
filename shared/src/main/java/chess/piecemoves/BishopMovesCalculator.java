package chess.piecemoves;

public class BishopMovesCalculator implements PieceMovesCalculator {
    private static final int[][] directions = { {-1, 1}, {1, 1}, {1, -1}, {-1, -1} };
    private static final boolean moveToObstruction = true;

    @Override
    public int[][] getDirections() {
        return directions;
    }

    @Override
    public boolean getMoveToObstruction() {
        return moveToObstruction;
    }
}
