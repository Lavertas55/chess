package chess.piecemoves;

public class RookMovesCalculator implements PieceMovesCalculator {
    private static final int[][] DIRECTIONS = { {-1, 0}, {0, 1}, {1, 0}, {0, -1} };
    private static final boolean MOVE_TO_OBSTRUCTION = true;

    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getDirections() {
        return DIRECTIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getMoveToObstruction() {
        return MOVE_TO_OBSTRUCTION;
    }
}
