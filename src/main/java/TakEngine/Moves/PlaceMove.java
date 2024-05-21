package TakEngine.Moves;

import TakEngine.Stone;

public class PlaceMove implements IMove {
  public final int Square;
  public final Stone Stone;

  public PlaceMove(int square, Stone stone) {
    Square = square;
    Stone = stone;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Square: ").append(Square).append("\n");
    sb.append("Side: ").append(Stone.getSide()).append("\n");
    sb.append("Stoneype: ").append(Stone.getStoneType()).append("\n");
    return sb.toString();
  }
}
