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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Square: ").append(Square).append("\n");
        sb.append("Direction: ").append(Direction).append("\n");
        sb.append("Flat Stones to Leave: ").append(FlatStonesToLeave).append("\n");
        return sb.toString(); 
    }
}
