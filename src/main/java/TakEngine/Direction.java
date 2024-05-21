package TakEngine;

public enum Direction {
  North,
  West,
  East,
  South;

  public int getShiftAmount(int boardSize) {
    switch (this) {
      case North -> {
        return -boardSize;
      }
      case West -> {
        return -1;
      }
      case East -> {
        return 1;
      }
      case South -> {
        return boardSize;
      }
    }
    return boardSize;
  }
}
