package chess.piecemoves;

public class KingMovesCalculator implements PieceMovesCalculator {
    private static final int[][] DIRECTIONS = { {-1, -1}, {-1, 0}, {-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1} };
    private static final boolean MOVE_TO_OBSTRUCTION = false;

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
