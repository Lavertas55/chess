package chess.piecemoves;

public class KnightMovesCalculator implements PieceMovesCalculator {
    private static final int[][] DIRECTIONS = { {-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1} };
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
