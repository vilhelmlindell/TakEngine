package TakEngine;

public enum Side {
  White,
  Black;

  // Method to get the opposite side
  public Side getOppositeSide() {
    if (this == White) {
      return Black;
    } else {
      return White;
    }
  }
}
