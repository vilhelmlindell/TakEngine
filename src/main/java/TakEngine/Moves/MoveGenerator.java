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
        BitHelper.iterateBits(~_board.OccupiedSquares, square -> {
            // On the first turn, an opponents flat stone is placed
            if (_board.Turn == 0) {
                Stone stone = new Stone(StoneType.FlatStone, _board.Side.getOppositeSide());
                placements.add(new Placement(square, stone));
            }
            else {
                if (_board.AvailableNormalStones[_board.Side.ordinal()] != 0) {
                    placements.add(new Placement(square, new Stone(StoneType.FlatStone, _board.Side)));
                    placements.add(new Placement(square, new Stone(StoneType.StandingStone, _board.Side)));
                }
                else if (_board.AvailableCapstones[_board.Side.ordinal()] != 0) {
                    placements.add(new Placement(square, new Stone(StoneType.Capstone, _board.Side)));
                }
            }
        });
        return placements;
    }
    private List<IMove> generateMovements() {
        List<IMove> movements = new ArrayList<>();
        long controlledSquares = _board.ControlledSquares[_board.Side.ordinal()];
        BitHelper.iterateBits(controlledSquares, square -> {
            long enemyStandingStones = _board.StandingStones[_board.Side.getOppositeSide().ordinal()];
            for (Direction direction : Direction.values()) {
                long shiftedStones = BitHelper.shiftBits(controlledSquares, _board.Size, direction);
                shiftedStones ^= enemyStandingStones;
            }
        });
        return movements;
    }

    private long getStackShifts(int square, long blockers) {
        long stackShifts = 0;
        stackShifts |=
    }
}
