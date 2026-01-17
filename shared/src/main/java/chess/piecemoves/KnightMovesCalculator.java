package chess.piecemoves;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private static final int[][] directions = { {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1} };
    private static final boolean moveToObstruction = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getDirections() {
        return directions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMoveToObstruction() {
        return moveToObstruction;
    }
}
