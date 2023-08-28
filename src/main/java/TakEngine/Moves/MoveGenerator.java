package TakEngine.Moves;

import TakEngine.*;

import java.util.ArrayList;
import java.util.List;

public class MoveGenerator {
    private final Board _board;

    public MoveGenerator(Board board) {
        _board = board;
    }

    public List<IMove> generateMoves() {
        List<IMove> moves = new ArrayList<>();
        moves.addAll(generatePlacements());
        moves.addAll(generateMovements());
        return moves;
    }

    private List<IMove> generatePlacements() {
        List<IMove> placements = new ArrayList<>();

        // Iterate the empty squares
        BitHelper.iterateBits(~_board.OccupiedSquares, false, square -> {
            // On the first turn, an opponents flat stone is placed
            if (_board.Turn == 0) {
                Stone stone = new Stone(StoneType.FlatStone, _board.Side.getOppositeSide());
                placements.add(new Placement(square, stone));
            } else {
                if (_board.AvailableNormalStones[_board.Side.ordinal()] != 0) {
                    placements.add(new Placement(square, new Stone(StoneType.FlatStone, _board.Side)));
                    placements.add(new Placement(square, new Stone(StoneType.StandingStone, _board.Side)));
                } else if (_board.AvailableCapstones[_board.Side.ordinal()] != 0) {
                    placements.add(new Placement(square, new Stone(StoneType.Capstone, _board.Side)));
                }
            }
        });
        return placements;
    }

    private List<IMove> generateMovements() {
        List<IMove> movements = new ArrayList<>();
        long controlledSquares = _board.ControlledSquares[_board.Side.ordinal()];
        BitHelper.iterateBits(controlledSquares, false, square -> {
            for (Direction direction : Direction.values()) {
                long blockers = _board.StandingStones[_board.Side.getOppositeSide().ordinal()] |
                        _board.Capstones[_board.Side.getOppositeSide().ordinal()];
                long stackMovementBits = getStackMovementBits(square, direction, blockers);
                boolean shouldIterateByMsb = direction == Direction.North || direction == Direction.West;
                BitHelper.iterateBits(stackMovementBits, shouldIterateByMsb, endSquare -> {
                });
            }
        });
        return movements;
    }

    private long getStackMovementBits(int square, Direction direction, long blockers) {
        int stackSize = _board.Stones[square].size();
        long movementBits = Tables.getLineSegmentBits(_board.Size, square, Direction.North, stackSize);
        if ((movementBits & blockers) != 0) {
            int blockerIndex = direction == Direction.North || direction == Direction.West ? Long.numberOfLeadingZeros(movementBits) : Long.numberOfTrailingZeros(movementBits);
            movementBits &= ~Tables.getLineBits(blockerIndex, square, Direction.North);
        }
        return movementBits;
    }

    private ArrayList<Integer> GenerateStackMoveCombinations() {
        int currentStackSize = 
    }
}
