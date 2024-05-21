package TakEngine.Moves;

import TakEngine.BitHelper;
import TakEngine.Board;
import TakEngine.Direction;
import TakEngine.Stone;
import TakEngine.StoneType;
import TakEngine.Tables;
import java.util.ArrayList;
import java.util.List;

record DropInfo(int numTraversable, boolean includeFlattening) {}

public class MoveGenerator {
  private final Board _board;

  public MoveGenerator(Board board) {
    _board = board;
  }

  public List<IMove> generateMoves() {
    List<IMove> moves = new ArrayList<>();
    moves.addAll(generatePlaceMoves());
    moves.addAll(generateStackMoves());
    return moves;
  }

  private List<IMove> generatePlaceMoves() {
    List<IMove> placements = new ArrayList<>();

    BitHelper.iterateBits(
        ~_board.OccupiedSquares,
        false,
        square -> {
          if (_board.Turn == 0) {
            Stone stone = new Stone(StoneType.FlatStone, _board.SideToMove.getOppositeSide());
            placements.add(new PlaceMove(square, stone));
          } else {
            StoneType[] stoneTypes =
                _board.AvailableNormalStones[_board.SideToMove.ordinal()] != 0
                    ? new StoneType[] {StoneType.FlatStone, StoneType.StandingStone}
                    : new StoneType[] {StoneType.Capstone};

            for (StoneType stoneType : stoneTypes) {
              placements.add(new PlaceMove(square, new Stone(stoneType, _board.SideToMove)));
            }
          }
        });

    return placements;
  }

  private List<IMove> generateStackMoves() {
    List<IMove> stackMoves = new ArrayList<>();
    long controlledSquares = _board.ControlledSquares[_board.SideToMove.ordinal()];

    // System.out.println(BitHelper.bitboardToString(controlledSquares, _board.Size));
    BitHelper.iterateBits(
        controlledSquares,
        false,
        square -> {
          // System.out.println(BitHelper.bitboardToString(blockers, _board.Size));
          for (Direction direction : Direction.values()) {
            DropInfo dropInfo = getDropInfo(square, direction);
            ArrayList<DropCombination> dropCombinations =
                Tables.getDropCombinations(
                    _board.Stacks[square].size(),
                    dropInfo.numTraversable(),
                    dropInfo.includeFlattening());

            for (DropCombination dropCombination : dropCombinations) {
              StackMove stackMove = new StackMove(square, direction, dropCombination);
              stackMoves.add(stackMove);
            }
          }
        });

    return stackMoves;
  }

  private DropInfo getDropInfo(int square, Direction direction) {
    long blockers =
        _board.StandingStones[_board.SideToMove.getOppositeSide().ordinal()]
            | _board.Capstones[_board.SideToMove.getOppositeSide().ordinal()];
    long squareBit = BitHelper.fromSquare(square);
    long capstone = (_board.Capstones[_board.SideToMove.ordinal()] & squareBit);
    long squaresToTraverse = 0;
    squaresToTraverse |= Tables.getLineRay(_board.Size, square, direction);

    long interceptedBlockers = (squaresToTraverse & blockers);

    boolean includeFlattening = false;

    if (interceptedBlockers != 0) {
      int blockerSquare;
      switch (direction) {
        case North:
        case West:
          blockerSquare = BitHelper.msb(interceptedBlockers, _board.Size);

        case South:
        case East:
        default:
          blockerSquare = BitHelper.lsb(interceptedBlockers);
      }
      squaresToTraverse &= ~Tables.getLineRay(_board.Size, blockerSquare, direction);

      if (capstone != 0) {
        long enemyCapstones = _board.Capstones[_board.SideToMove.getOppositeSide().ordinal()];
        long blockerSquareBit = BitHelper.fromSquare(blockerSquare);

        // Check that the blocker is standing stone
        if ((enemyCapstones & blockerSquareBit) == 0) {
          squaresToTraverse |= blockerSquareBit;
          includeFlattening = true;
        }
      }
    }

    return new DropInfo(Long.bitCount(squaresToTraverse), includeFlattening);
  }

  // private List<Integer> getStackTraversableSquares(int square, Direction direction, long
  // blockers) {
  //  int stackSize = _board.Stacks[square].size();
  //  long movementBits = Tables.getLineSegment(_board.Size, square, direction, stackSize);
  //  System.out.println(BitHelper.bitboardToString(movementBits, _board.Size));

  //  if ((movementBits & blockers) != 0) {
  //    int blockerIndex =
  //        direction == Direction.North || direction == Direction.West
  //            ? Long.numberOfLeadingZeros(movementBits)
  //            : Long.numberOfTrailingZeros(movementBits);
  //    movementBits &= ~Tables.getLineRay(blockerIndex, square, Direction.North);
  //  }

  //  List<Integer> traversableSquares = new ArrayList<>();
  //  boolean shouldIterateByMsb = direction == Direction.North || direction == Direction.West;
  //  BitHelper.iterateBits(movementBits, shouldIterateByMsb, traversableSquares::add);

  //  return traversableSquares;
  // }
}
