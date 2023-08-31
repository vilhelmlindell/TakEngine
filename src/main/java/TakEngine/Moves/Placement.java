package TakEngine.Moves;

import TakEngine.Stone;

public class Placement implements IMove {
    public final int Square;
    public final Stone Stone;

    public Placement(int square, Stone stone) {
        Square = square;
        Stone = stone;
    }
}
