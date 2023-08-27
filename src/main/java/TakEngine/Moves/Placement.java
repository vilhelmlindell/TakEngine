package TakEngine.Moves;

import TakEngine.Stone;

public class Placement implements IMove {
    public final int Square;
    public final Stone StoneType;

    public Placement(int square, Stone stoneType) {
        Square = square;
        StoneType = stoneType;
    }
}
