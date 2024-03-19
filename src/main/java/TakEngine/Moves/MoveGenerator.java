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

        BitHelper.iterateBits(~_board.OccupiedSquares, _board.Size, false, square -> {
            if (_board.Turn == 0) {
                Stone stone = new Stone(StoneType.FlatStone, _board.SideToMove.getOppositeSide());
                placements.add(new Placement(square, stone));
            } else {
                StoneType[] stoneTypes = _board.AvailableNormalStones[_board.SideToMove.ordinal()] != 0
                        ? new StoneType[] { StoneType.FlatStone, StoneType.StandingStone }
                        : new StoneType[] { StoneType.Capstone };

                for (StoneType stoneType : stoneTypes) {
                    placements.add(new Placement(square, new Stone(stoneType, _board.SideToMove)));
                }
            }
        });

        return placements;
    }

    private List<IMove> generateMovements() {
        List<IMove> movements = new ArrayList<>();
        long controlledSquares = _board.ControlledSquares[_board.SideToMove.ordinal()];

        //System.out.println(BitHelper.bitboardToString(controlledSquares, _board.Size));
        BitHelper.iterateBits(controlledSquares, _board.Size, false, startSquare -> {
            for (Direction direction : Direction.values()) {
                long blockers = _board.StandingStones[_board.SideToMove.getOppositeSide().ordinal()]
                        | _board.Capstones[_board.SideToMove.getOppositeSide().ordinal()];

                int stackSize = _board.Stacks[startSquare].size();
                List<Integer> stackTraversableSquares = getStackTraversableSquares(startSquare, direction, blockers);
                List<List<Integer>> stackMovementCombinations = new ArrayList<>();
                generateStackMoveCombinations(stackMovementCombinations, new ArrayList<>(), stackSize, stackTraversableSquares.size());
                System.out.println(stackTraversableSquares);
                //System.out.println(stackMovementCombinations);

                for (List<Integer> stackMovement : stackMovementCombinations) {
                    movements.add(new Movement(startSquare, direction, stackMovement));
                }
            }
        });

        return movements;
    }

    private List<Integer> getStackTraversableSquares(int square, Direction direction, long blockers) {
        int stackSize = _board.Stacks[square].size();
        long movementBits = Tables.getLineSegment(_board.Size, square, Direction.North, stackSize);

        if ((movementBits & blockers) != 0) {
            int blockerIndex = direction == Direction.North || direction == Direction.West
                    ? Long.numberOfLeadingZeros(movementBits)
                    : Long.numberOfTrailingZeros(movementBits);
            movementBits &= ~Tables.getLineRay(blockerIndex, square, Direction.North);
        }

        List<Integer> traversableSquares = new ArrayList<>();
        boolean shouldIterateByMsb = direction == Direction.North || direction == Direction.West;
        BitHelper.iterateBits(movementBits, _board.Size, shouldIterateByMsb, traversableSquares::add);

        return traversableSquares;
    } 
    private void generateStackMoveCombinations(List<List<Integer>> combinations, List<Integer> currentCombination, int stackSize, int possibleTraversableSquares) {
        if (stackSize == 0 || possibleTraversableSquares == 0) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        for (int stonesToLeave = 1; stonesToLeave <= stackSize; stonesToLeave++) {
            currentCombination.add(stonesToLeave);
            generateStackMoveCombinations(combinations, currentCombination, stackSize - stonesToLeave, possibleTraversableSquares - 1);
            currentCombination.remove(currentCombination.size() - 1); // Backtrack
        }
    } 
}

