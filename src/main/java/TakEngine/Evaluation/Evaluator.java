package TakEngine.Evaluation;

import TakEngine.Board;
import TakEngine.Side;

public class Evaluator {
  private final Board _board;

  public Evaluator(Board board) {
    _board = board;
  }

  public float evaluate() {
    float evaluation = 0f;
    evaluation += evaluateControlledSquares();
    return evaluation;
  }

  private float evaluateControlledSquares() {
    float evaluation = 0f;
    evaluation += Long.bitCount(_board.ControlledSquares[Side.White.ordinal()]);
    evaluation -= Long.bitCount(_board.ControlledSquares[Side.Black.ordinal()]);
    return evaluation;
  }
}
