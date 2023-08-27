package TakEngine.Moves;

import TakEngine.BitHelper;
import TakEngine.Direction;

import java.lang.reflect.Array;

interface ComputeTable<T> {
    T apply(int boardSize);
}

public class Tables {
    public static final int[][][] SquaresToEdge = computeForAllBoardSizes(int[][].class, Tables::computeSquaresToEdge);
    public static final long[][] BoundaryLineMasks = computeForAllBoardSizes(long[].class, Tables::computeBoundaryLineMasks);
    public static final long[][][] LineMasks = computeForAllBoardSizes(long[][].class, Tables::computeLineMasks);
    public static final long[][][] ShiftLineMasks = computeForAllBoardSizes(long[][].class, Tables::computeShiftLineMasks);

    private static <T> T[] computeForAllBoardSizes(Class<T> typeClass, ComputeTable<T> computeTable) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(typeClass, 8);
        for (int boardSize = 3; boardSize <= 8; boardSize++) {
            array[boardSize] = computeTable.apply(boardSize);
        }
        return array;
    }
    private static long[] computeBoundaryLineMasks(int boardSize) {
        long[] boundaryLineMasks = new long[4];
        for (Direction direction : Direction.values()) {
            switch (direction) {
                case North -> {
                    boundaryLineMasks[direction.ordinal()] = computeRankMask(0, boardSize);
                }
                case West -> {
                    boundaryLineMasks[direction.ordinal()] = computeFileMask(0, boardSize);
                }
                case East -> {
                    boundaryLineMasks[direction.ordinal()] = computeFileMask(boardSize - 1, boardSize);
                }
                case South -> {
                    boundaryLineMasks[direction.ordinal()] = computeRankMask(boardSize - 1, boardSize);
                }
            }
        }

        return boundaryLineMasks;
    }
    private static long[][] computeLineMasks(int boardSize) {
        long[][] rayMasks = new long[boardSize * boardSize][4];
        for (int square = 0; square < boardSize * boardSize; square++) {
            long[] squareRayMasks = new long[4];
            for (Direction direction : Direction.values()) {
                long rayMask = 0;
                int maxSquaresToEdge = SquaresToEdge[boardSize][square][direction.ordinal()];
                for (int squaresToEdge = 1; squaresToEdge < maxSquaresToEdge; squaresToEdge++){
                    int endSquare = square + direction.getShiftAmount(boardSize) * squaresToEdge;
                    rayMask = BitHelper.setBit(rayMask, endSquare);
                }
                squareRayMasks[direction.ordinal()] = rayMask;
            }
            rayMasks[square] = squareRayMasks;
        }
        return rayMasks;
    }
    private static long[][] computeShiftLineMasks(int boardSize) {
        int maxShiftDistance = boardSize - 1;
        long[][] shiftLineMasks = new long[boardSize * boardSize][maxShiftDistance];
        for (int square = 0; square < boardSize * boardSize; square++) {
            long[] squareShiftLineMask = new long[maxShiftDistance];
            for (int shiftDistance = 1; shiftDistance <= maxShiftDistance; shiftDistance++) {
                long shiftLineMask = 0;
                for (Direction direction : Direction.values()) {
                    int maxSquaresToEdge = SquaresToEdge[boardSize][square][direction.ordinal()];
                    for (int squaresToEdge = 1; squaresToEdge < maxSquaresToEdge; squaresToEdge++) {
                        int actualShiftDistance = Math.min(shiftDistance, squaresToEdge);
                        int endSquare = square + direction.getShiftAmount(boardSize) * actualShiftDistance;
                        shiftLineMask = BitHelper.setBit(shiftLineMask, endSquare);
                    }
                }
            }
            shiftLineMasks[square] = squareShiftLineMask;
        }
        return shiftLineMasks;
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
}
