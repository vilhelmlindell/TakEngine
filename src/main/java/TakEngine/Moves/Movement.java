package TakEngine.Moves;

import TakEngine.Direction;

import java.util.List;

public class Movement implements IMove {
    public final int Square;
    public final Direction Direction;
    public final List<Integer> FlatStonesToLeave;

    public Movement(int square, Direction direction, List<Integer> flatStonesToLeave) {
        Square = square;
        Direction = direction;
        FlatStonesToLeave = flatStonesToLeave;
    }
}
