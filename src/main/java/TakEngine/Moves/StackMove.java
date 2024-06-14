package TakEngine.Moves;

import TakEngine.Direction;

public class StackMove implements IMove {
  public final int Square;
  public final Direction Direction;
  public final DropCombination DropCombination;

  public StackMove(int square, Direction direction, DropCombination dropCombination) {
    Square = square;
    Direction = direction;
    DropCombination = dropCombination;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Square: ").append(Square).append("\n");
    sb.append("Direction: ").append(Direction).append("\n");
    sb.append("Drop Combination: ");
    for (int dropCount : DropCombination) {
      sb.append(dropCount);
    }
    sb.append("\n");
    return sb.toString();
  }
}
