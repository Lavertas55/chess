package chess.piecemoves;

public class KingMovesCalculator implements PieceMovesCalculator {
    private static final int[][] directions = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };
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
