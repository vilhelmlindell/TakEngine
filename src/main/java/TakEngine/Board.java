package TakEngine;

import TakEngine.Moves.IMove;
import TakEngine.Moves.MoveGenerator;
import TakEngine.Moves.PlaceMove;
import TakEngine.Moves.StackMove;
import java.util.ArrayList;
import java.util.List;

public final class Board {
  public static final int Sides = 2;

  public final int Size;
  public final int SquareCount;
  public final int[][] FlatStones;
  public final long[] Capstones = new long[Sides];
  public final long[] StandingStones = new long[Sides];
  public final long[] ControlledSquares = new long[Sides];

  public Side SideToMove = TakEngine.Side.White;
  public int Turn = 0;
  public long OccupiedSquares = 0;

  public int[] AvailableCapstones;
  public int[] AvailableNormalStones;

  public final MoveGenerator MoveGenerator;

  public Board(GameConfiguration configuration) {
    Size = configuration.Size;
    SquareCount = Size * Size;

    AvailableCapstones = new int[] { configuration.Capstones, configuration.Capstones };
    FlatStones = new int[Sides][SquareCount];

    MoveGenerator = new MoveGenerator(this);
  }

  public Board(int size) {
    this(GameConfiguration.BySize.get(size));
  }

  public List<IMove> generateMoves() {
    return MoveGenerator.generateMoves();
  }

  public void makeMove(IMove move) {
    if (move instanceof PlaceMove placement) {
      place(placement);
    } else if (move instanceof StackMove movement) {
      move(movement);
    }
  }

  public void unmakeMove(IMove move) {
    if (move instanceof PlaceMove placement) {
      switch (placement.Stone.getStoneType()) {
        case Capstone -> {
          Capstones[SideToMove.ordinal()] = BitHelper.clearBit(Capstones[SideToMove.ordinal()], placement.Square);
        }
        case FlatStone -> {
          StandingStones[SideToMove.ordinal()] = BitHelper.clearBit(StandingStones[SideToMove.ordinal()],
              placement.Square);
        }
        case StandingStone -> {
          FlatStones[SideToMove.ordinal()][placement.Square] = 0;
        }
      }
    } else if (move instanceof StackMove movement) {
      // int originalStack = 0;
      // int originalStackSize = 0;
      // for (int i = movement.DropCombination.Length; i >= 0; i--) {
      // int stoneCount = movement.DropCombination.get(i);
      // originalStackSize += stoneCount;
      // int square = movement.Square + movement.Direction.getShiftAmount(Size) * i;
      // }
      // move(movement);
    }

    // if (isRemoval) {
    // }
  }

  public void addStone(int square, Stone stone) {
    BitHelper.setBit(OccupiedSquares, square);
    BitHelper.setBit(ControlledSquares[SideToMove.ordinal()], square);
    switch (stone.getStoneType()) {
      case FlatStone:
      case StandingStone:
      case Capstone:
    }
  }

  public void place(PlaceMove placement) {
    if (placement.Stone.getStoneType() == StoneType.Capstone) {
      AvailableCapstones[placement.Stone.getSide().ordinal()] -= 1;
    } else {
      AvailableNormalStones[placement.Stone.getSide().ordinal()] -= 1;
    }
  }

  // public void move(StackMove stackMove) {
  // List<Stone> stonesToTake = removeStones(
  // stackMove.Square, Stacks[stackMove.Square].size() -
  // stackMove.DropCombination.get(0));
  // int squareCount = 1;
  // for (int stonesToLeave : stackMove.DropCombination) {
  // int directionalOffset = stackMove.Direction.getShiftAmount(Size) *
  // squareCount;
  // int square = stackMove.Square + directionalOffset;
  // ArrayList<Stone> stonesToAdd = (ArrayList<Stone>) stonesToTake.subList(0,
  // stonesToLeave);
  // addStones(square, stonesToAdd);
  // squareCount += 1;
  // }
  // }

  public boolean getLongestLineLength() {
    long controlledSquares = ControlledSquares[SideToMove.ordinal()] ^ StandingStones[SideToMove.ordinal()];

    // System.out.println("1");
    // Check for vertical win
    int previousStoneSquare = 0;
    for (int rankIndex = 0; rankIndex < Size; rankIndex++) {
      // System.out.println("2");
      long rank = Tables.getRank(Size, rankIndex);
      long controlledStone = (rank & controlledSquares);
      int stoneSquare = Long.numberOfTrailingZeros(controlledStone);

      // Check if rank has a controlled stone
      if (controlledStone == 0) {
        break;
      }

      // System.out.println("5");
      int shiftedStoneSquare = stoneSquare + Direction.North.getShiftAmount(Size);
      if (shiftedStoneSquare < 0) {
        return true;
      }
      long stoneLine = Tables.getLineBetweenSquares(Size, previousStoneSquare, shiftedStoneSquare);
      // System.out.println("4");

      previousStoneSquare = stoneSquare;

      // Check if the stones between this and the previous rank are connected
      if ((stoneLine & controlledSquares) != stoneLine) {
        break;
      }

      if (rankIndex == Size - 1) {
        return true;
      }
    }
    // Check for horizontal win
    for (int fileIndex = 0; fileIndex < Size; fileIndex++) {
      // System.out.println("3");
      long file = Tables.getFile(Size, fileIndex);
      long controlledStone = (file & controlledSquares);
      int stoneSquare = Long.numberOfTrailingZeros(controlledStone);
      // System.out.println(BitHelper.bitboardToString(controlledStone, Size));
      // System.out.println(BitHelper.bitboardToString(file, Size));

      // Check if rank has a controlled stone
      if (controlledStone == 0) {
        break;
      }

      int shiftedStoneSquare = stoneSquare + Direction.West.getShiftAmount(Size);
      long stoneLine = Tables.getLineBetweenSquares(Size, previousStoneSquare, shiftedStoneSquare);

      previousStoneSquare = stoneSquare;

      // Check if the stones between this and the previous rank are connected
      if ((stoneLine & controlledSquares) != stoneLine) {
        break;
      }

      if (fileIndex == Size - 1) {
        return true;
      }
    }
    return false;
  }

  public boolean isGameWon() {
    long controlledSquares = ControlledSquares[SideToMove.ordinal()] ^ StandingStones[SideToMove.ordinal()];

    // Check for vertical win
    int previousStoneSquare = 0;
    for (int rankIndex = 0; rankIndex < Size; rankIndex++) {
      long rank = Tables.getRank(Size, rankIndex);
      long controlledStone = (rank & controlledSquares);
      int stoneSquare = Long.numberOfTrailingZeros(controlledStone);

      // Check if rank has a controlled stone
      if (controlledStone == 0) {
        break;
      }

      int shiftedStoneSquare = stoneSquare + Direction.North.getShiftAmount(Size);
      if (shiftedStoneSquare < 0) {
        return true;
      }
      long stoneLine = Tables.getLineBetweenSquares(Size, previousStoneSquare, shiftedStoneSquare);

      previousStoneSquare = stoneSquare;

      // Check if the stones between this and the previous rank are connected
      if ((stoneLine & controlledSquares) != stoneLine) {
        break;
      }

      if (rankIndex == Size - 1) {
        return true;
      }
    }
    // Check for horizontal win
    for (int fileIndex = 0; fileIndex < Size; fileIndex++) {
      long file = Tables.getFile(Size, fileIndex);
      long controlledStone = (file & controlledSquares);
      int stoneSquare = Long.numberOfTrailingZeros(controlledStone);

      // Check if rank has a controlled stone
      if (controlledStone == 0) {
        break;
      }

      int shiftedStoneSquare = stoneSquare + Direction.West.getShiftAmount(Size);
      long stoneLine = Tables.getLineBetweenSquares(Size, previousStoneSquare, shiftedStoneSquare);

      previousStoneSquare = stoneSquare;

      // Check if the stones between this and the previous rank are connected
      if ((stoneLine & controlledSquares) != stoneLine) {
        break;
      }

      if (fileIndex == Size - 1) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    char[] lettersByStoneType = new char[] { 'c', 'f', 's' };

    String[] stackStrings = new String[Stacks.length];

    for (int square = 0; square < stackStrings.length; square++) {
      ArrayList<Stone> stack = Stacks[square];

      StringBuilder stackString = new StringBuilder();

      for (Stone stone : stack) {
        char stoneLetter = lettersByStoneType[stone.getStoneType().ordinal()];
        if (stone.getSide() == Side.White) {
          stoneLetter = Character.toUpperCase(stoneLetter);
        }
        stackString.append(stoneLetter);
      }

      if (stack.isEmpty()) {
        stackString.append(' ');
      }

      stackStrings[square] = stackString.toString();
    }

    int[] highestFileStackSizes = new int[Size];

    for (int file = 0; file < Size; file++) {
      int highestFileStackSize = 0;
      for (int rank = 0; rank < Size; rank++) {
        int square = file + rank * Size;
        int fileStackSize = stackStrings[square].length();

        if (fileStackSize > highestFileStackSize) {
          highestFileStackSize = fileStackSize;
        }
      }
      highestFileStackSizes[file] = highestFileStackSize;
    }

    StringBuilder result = getBitboardStringBuilder(stackStrings, highestFileStackSizes);

    return result.toString();
  }

  private StringBuilder getBitboardStringBuilder(
      String[] stackStrings, int[] highestFileStackSizes) {
    StringBuilder result = new StringBuilder();

    for (int rank = 0; rank < Size; rank++) {
      StringBuilder rankString = new StringBuilder();

      for (int file = 0; file < Size; file++) {
        int square = file + rank * Size;
        String stackString = stackStrings[square];
        int additionalSpaces = highestFileStackSizes[file] - stackString.length();
        rankString.append("| ");
        rankString.append(stackString);
        rankString.append(" ".repeat(additionalSpaces));
        rankString.append(" ");
        if (file == Size - 1) {
          rankString.append("|\n");
        }
      }
      String string = rankString.toString();
      result.append("-".repeat(string.length() - 1));
      result.append('\n');
      result.append(rankString.toString());

      if (rank == Size - 1) {
        result.append("-".repeat(string.length() - 1));
      }
    }
    return result;
  }
}
