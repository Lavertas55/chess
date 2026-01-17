package chess.piecemoves;

public class BishopMovesCalculator implements PieceMovesCalculator {
    private static final int[][] directions = { {-1, 1}, {1, 1}, {1, -1}, {-1, -1} };
    private static final boolean moveToObstruction = true;

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
