package TakEngine;

import TakEngine.Board;

public class Evaluation {
    public float evaluate(Board board) {
        float evaluation = 0f;
        evaluation += evaluateControlledSquares(board);
        return evaluation;
    }
    private float evaluateControlledSquares(Board board) {
        float evaluation = 0f;
        evaluation += Long.bitCount(board.ControlledSquares[Side.White.ordinal()]);
        evaluation -= Long.bitCount(board.ControlledSquares[Side.Black.ordinal()]);
        return evaluation;
    }
}
