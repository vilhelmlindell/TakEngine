package TakEngine;

import TakEngine.Moves.DropCombination;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

interface ComputeTable<T> {
  T apply(int boardSize);
}

public final class Tables {
  private static final long[][] _ranks =
      computeForAllBoardSizes(long[].class, Tables::computeRanks);
  private static final long[][] _files =
      computeForAllBoardSizes(long[].class, Tables::computeFiles);
  private static final int[][][] _squaresToEdge =
      computeForAllBoardSizes(int[][].class, Tables::computeSquaresToEdge);
  private static final long[][] _boundaryLines =
      computeForAllBoardSizes(long[].class, Tables::computeBoundaryLines);
  private static final long[][][] _lineRays =
      computeForAllBoardSizes(long[][].class, Tables::computeLineRays);
  private static final long[][][] _linesBetweenSquares =
      computeForAllBoardSizes(long[][].class, Tables::computeLinesBetweenSquares);
  private static final long[][][][] _lineSegments =
      computeForAllBoardSizes(long[][][].class, Tables::computeLineSegments);
  private static final ArrayList<DropCombination>[][][] _dropCombinations =
      computeDropCombinations();

  public static int getSquaresToEdge(int boardSize, int square, Direction direction) {
    return _squaresToEdge[boardSize - 3][square][direction.ordinal()];
  }

  public static long getLineSegment(int boardSize, int square, Direction direction, int length) {
    return _lineSegments[boardSize - 3][square][direction.ordinal()][length - 1];
  }

  public static long getLineBetweenSquares(int boardSize, int square, int endSquare) {
    return _linesBetweenSquares[boardSize - 3][square][endSquare];
  }

  public static long getLineRay(int boardSize, int square, Direction direction) {
    return _lineRays[boardSize - 3][square][direction.ordinal()];
  }

  public static long getBoundaryLine(int boardSize, Direction direction) {
    return _boundaryLines[boardSize - 3][direction.ordinal()];
  }

  public static long getRank(int boardSize, int rankIndex) {
    return _ranks[boardSize - 3][rankIndex];
  }

  public static long getFile(int boardSize, int fileIndex) {
    return _files[boardSize - 3][fileIndex];
  }

  public static ArrayList<DropCombination> getDropCombinations(
      int stackSize, int numSquares, boolean includeFlattening) {
    return _dropCombinations[stackSize][numSquares][includeFlattening ? 1 : 0];
  }

  private static <T> T[] computeForAllBoardSizes(Class<T> typeClass, ComputeTable<T> computeTable) {
    @SuppressWarnings("unchecked")
    T[] array = (T[]) Array.newInstance(typeClass, 6);
    for (int boardSize = 3; boardSize <= 8; boardSize++) {
      array[boardSize - 3] = computeTable.apply(boardSize);
    }
    return array;
  }

  private static int[][] computeSquaresToEdge(int boardSize) {
    int[][] squaresToEdge = new int[boardSize * boardSize][4];

    for (int file = 0; file < boardSize; file++) {
      for (int rank = 0; rank < boardSize; rank++) {
        int south = boardSize - 1 - rank;
        int east = boardSize - 1 - file;

        int square = rank * boardSize + file;

        squaresToEdge[square][Direction.North.ordinal()] = rank;
        squaresToEdge[square][Direction.South.ordinal()] = south;
        squaresToEdge[square][Direction.West.ordinal()] = file;
        squaresToEdge[square][Direction.East.ordinal()] = east;
      }
    }

    return squaresToEdge;
  }

  private static long[] computeBoundaryLines(int boardSize) {
    long[] boundaryLine = new long[4];
    for (Direction direction : Direction.values()) {
      switch (direction) {
        case North -> {
          boundaryLine[direction.ordinal()] = getRank(boardSize, 0);
        }
        case West -> {
          boundaryLine[direction.ordinal()] = getFile(boardSize, 0);
        }
        case East -> {
          boundaryLine[direction.ordinal()] = getFile(boardSize, boardSize - 1);
        }
        case South -> {
          boundaryLine[direction.ordinal()] = getRank(boardSize, boardSize - 1);
        }
      }
    }

    return boundaryLine;
  }

  private static long[][] computeLinesBetweenSquares(int boardSize) {
    int squareCount = boardSize * boardSize;
    long[][] lineRays = new long[squareCount][squareCount];
    for (int square = 0; square < squareCount; square++) {
      long[] lineRaysFromSquare = new long[64];
      for (Direction direction : Direction.values()) {
        int maxSquaresToEdge = getSquaresToEdge(boardSize, square, direction);
        for (int squaresToEdge = 0; squaresToEdge <= maxSquaresToEdge; squaresToEdge++) {
          int endSquare = square + direction.getShiftAmount(boardSize) * squaresToEdge;
          lineRaysFromSquare[endSquare] =
              getLineRay(boardSize, square, direction)
                  ^ getLineRay(boardSize, endSquare, direction);
        }
      }
      lineRays[square] = lineRaysFromSquare;
    }
    return lineRays;
  }

  private static long[][] computeLineRays(int boardSize) {
    long[][] lineRays = new long[boardSize * boardSize][4];
    for (int square = 0; square < boardSize * boardSize; square++) {
      long[] lineRaysFromSquare = new long[4];
      for (Direction direction : Direction.values()) {
        long lineRay = 0;
        int maxSquaresToEdge = getSquaresToEdge(boardSize, square, direction);
        for (int squaresToEdge = 1; squaresToEdge <= maxSquaresToEdge; squaresToEdge++) {
          int endSquare = square + direction.getShiftAmount(boardSize) * squaresToEdge;
          lineRay = BitHelper.setBit(lineRay, endSquare);
        }
        lineRaysFromSquare[direction.ordinal()] = lineRay;
      }
      lineRays[square] = lineRaysFromSquare;
    }
    return lineRays;
  }

  private static long[][][] computeLineSegments(int boardSize) {
    int maxShiftDistance = boardSize - 1;
    long[][][] lineSegments =
        new long[boardSize * boardSize][maxShiftDistance][Direction.values().length];
    for (int square = 0; square < boardSize * boardSize; square++) {
      long[][] lineSegmentsFromSquare = new long[Direction.values().length][maxShiftDistance];
      for (Direction direction : Direction.values()) {
        long[] lineSegmentsFromDirection = new long[maxShiftDistance];
        for (int shiftDistance = 1; shiftDistance <= maxShiftDistance; shiftDistance++) {
          long lineSegment = 0;
          int maxSquaresToEdge = getSquaresToEdge(boardSize, square, direction);
          for (int squaresToEdge = 1; squaresToEdge <= maxSquaresToEdge; squaresToEdge++) {
            int actualShiftDistance = Math.min(shiftDistance, squaresToEdge);
            int endSquare = square + direction.getShiftAmount(boardSize) * actualShiftDistance;
            lineSegment = BitHelper.setBit(lineSegment, endSquare);
          }
          lineSegmentsFromDirection[shiftDistance - 1] = lineSegment;
        }
        lineSegmentsFromSquare[direction.ordinal()] = lineSegmentsFromDirection;
      }
      lineSegments[square] = lineSegmentsFromSquare;
    }
    return lineSegments;
  }

  private static long[] computeRanks(int boardSize) {
    long[] ranks = new long[boardSize];
    for (int rankIndex = 0; rankIndex < boardSize; rankIndex++) {
      long rank = 0;
      for (int fileIndex = 0; fileIndex < boardSize; fileIndex++) {
        int square = rankIndex * boardSize + fileIndex;
        rank = BitHelper.setBit(rank, square);
      }
      ranks[rankIndex] = rank;
    }
    return ranks;
  }

  private static long[] computeFiles(int boardSize) {
    long[] files = new long[boardSize];
    for (int fileIndex = 0; fileIndex < boardSize; fileIndex++) {
      long file = 0;
      for (int rankIndex = 0; rankIndex < boardSize; rankIndex++) {
        int square = rankIndex * boardSize + fileIndex;
        file = BitHelper.setBit(file, square);
      }
      files[fileIndex] = file;
    }
    return files;
  }

  private static ArrayList<DropCombination>[][][] computeDropCombinations() {
    @SuppressWarnings("unchecked")
    ArrayList<DropCombination>[][][] allDropCombinations = new ArrayList[8][8][2];
    for (int stackSize = 1; stackSize <= 8; stackSize++) {
      for (int numTraversable = 1; numTraversable <= Math.max(stackSize - 1, 1); numTraversable++) {
        for (final boolean includeFlattening : new boolean[] {false, true}) {
          List<List<Integer>> combinations = new ArrayList<>();
          generateStackMoveCombinations(
              stackSize, numTraversable, includeFlattening, new ArrayList<>(), combinations);

          ArrayList<DropCombination> dropCombinations =
              combinations.stream()
                  .map(DropCombination::new)
                  .collect(Collectors.toCollection(ArrayList::new));

          allDropCombinations[stackSize][numTraversable][includeFlattening ? 1 : 0] =
              dropCombinations;
        }
      }
    }
    return allDropCombinations;
  }

  private static void generateStackMoveCombinations(
      int stackSize,
      int numTraversable,
      boolean includeFlattening,
      List<Integer> currentCombination,
      List<List<Integer>> combinations) {
    if (stackSize == 0 || numTraversable == 0) {
      return;
    }

    // The last traversable square is a standing stone which can be flattened
    if (includeFlattening && numTraversable == 1) {
      currentCombination.add(1);
    }

    int startIndex = currentCombination.isEmpty() ? 0 : 1;
    for (int stonesToLeave = startIndex; stonesToLeave <= stackSize; stonesToLeave++) {
      currentCombination.add(stonesToLeave);
      combinations.add(new ArrayList<>(currentCombination));
      generateStackMoveCombinations(
          stackSize - stonesToLeave,
          numTraversable - 1,
          includeFlattening,
          currentCombination,
          combinations);
      currentCombination.remove(currentCombination.size() - 1); // Backtrack
    }
  }
}
