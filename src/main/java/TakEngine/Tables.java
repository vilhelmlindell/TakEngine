package TakEngine;

import java.lang.reflect.Array;

interface ComputeTable<T> {
    T apply(int boardSize);
}

public class Tables {
    private static final int[][][] _squaresToEdge = computeForAllBoardSizes(int[][].class, Tables::computeSquaresToEdge);
    private static final long[][] _boundaryLineBits = computeForAllBoardSizes(long[].class, Tables::computeBoundaryLineBits);
    private static final long[][][] _lineBits = computeForAllBoardSizes(long[][].class, Tables::computeLineBits);
    private static final long[][][][] _lineSegmentBits = computeForAllBoardSizes(long[][][].class, Tables::computeLineSegmentBits);

    public static long getLineSegmentBits(int boardSize, int square, Direction direction, int length) {
        return _lineSegmentBits[boardSize][square][direction.ordinal()][length];
    }

    public static long getLineBits(int boardSize, int square, Direction direction) {
        return _lineBits[boardSize][square][direction.ordinal()];
    }

    public static long getBoundaryLineBits(int boardSize, Direction direction) {
        return _boundaryLineBits[boardSize][direction.ordinal()];
    }

    private static <T> T[] computeForAllBoardSizes(Class<T> typeClass, ComputeTable<T> computeTable) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(typeClass, 8);
        for (int boardSize = 3; boardSize <= 8; boardSize++) {
            array[boardSize] = computeTable.apply(boardSize);
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

    private static long[] computeBoundaryLineBits(int boardSize) {
        long[] boundaryLineBits = new long[4];
        for (Direction direction : Direction.values()) {
            switch (direction) {
                case North -> {
                    boundaryLineBits[direction.ordinal()] = computeRankMask(0, boardSize);
                }
                case West -> {
                    boundaryLineBits[direction.ordinal()] = computeFileMask(0, boardSize);
                }
                case East -> {
                    boundaryLineBits[direction.ordinal()] = computeFileMask(boardSize - 1, boardSize);
                }
                case South -> {
                    boundaryLineBits[direction.ordinal()] = computeRankMask(boardSize - 1, boardSize);
                }
            }
        }

        return boundaryLineBits;
    }

    private static long[][] computeLineBits(int boardSize) {
        long[][] lineBitsArray = new long[boardSize * boardSize][4];
        for (int square = 0; square < boardSize * boardSize; square++) {
            long[] squareLineBits = new long[4];
            for (Direction direction : Direction.values()) {
                long lineBits = 0;
                int maxSquaresToEdge = _squaresToEdge[boardSize][square][direction.ordinal()];
                for (int squaresToEdge = 1; squaresToEdge < maxSquaresToEdge; squaresToEdge++) {
                    int endSquare = square + direction.getShiftAmount(boardSize) * squaresToEdge;
                    lineBits = BitHelper.setBit(lineBits, endSquare);
                }
                squareLineBits[direction.ordinal()] = lineBits;
            }
            lineBitsArray[square] = squareLineBits;
        }
        return lineBitsArray;
    }

    private static long[][][] computeLineSegmentBits(int boardSize) {
        int maxShiftDistance = boardSize - 1;
        long[][][] shiftLineBits = new long[boardSize * boardSize][maxShiftDistance][Direction.values().length];
        for (int square = 0; square < boardSize * boardSize; square++) {
            long[][] squareShiftLineBits = new long[maxShiftDistance][Direction.values().length];
            for (int shiftDistance = 1; shiftDistance <= maxShiftDistance; shiftDistance++) {
                long[] directionShiftLineBits = new long[4];
                for (Direction direction : Direction.values()) {
                    long shiftLineMask = 0;
                    int maxSquaresToEdge = _squaresToEdge[boardSize][square][direction.ordinal()];
                    for (int squaresToEdge = 1; squaresToEdge < maxSquaresToEdge; squaresToEdge++) {
                        int actualShiftDistance = Math.min(shiftDistance, squaresToEdge);
                        int endSquare = square + direction.getShiftAmount(boardSize) * actualShiftDistance;
                        shiftLineMask = BitHelper.setBit(shiftLineMask, endSquare);
                    }
                    directionShiftLineBits[direction.ordinal()] = shiftLineMask;
                }
                squareShiftLineBits[square] = directionShiftLineBits;
            }
            shiftLineBits[square] = squareShiftLineBits;
        }
        return shiftLineBits;
    }

    private static long computeRankMask(int rank, int boardSize) {
        long mask = 0;
        for (int file = 0; file < boardSize; file++) {
            int square = rank * boardSize + file;
            mask = BitHelper.setBit(mask, square);
        }
        return mask;
    }

    private static long computeFileMask(int file, int boardSize) {
        long mask = 0;
        for (int rank = 0; rank < boardSize; rank++) {
            int square = rank * boardSize + file;
            mask = BitHelper.setBit(mask, square);
        }
        return mask;
    }
}
