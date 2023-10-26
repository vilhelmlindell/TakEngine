package TakEngine;

import TakEngine.Moves.IMove;
import TakEngine.Moves.MoveGenerator;
import TakEngine.Moves.Movement;
import TakEngine.Moves.Placement;

import java.util.ArrayList;
import java.util.List;

public final class Board {
    public static final int Sides = 2;

    public final int Size;
    public final int SquareCount;
    public final ArrayList<Stone>[] Stacks;
    public final int[][] FlatStones;
    public final long[] Capstones = new long[Sides];
    public final long[] StandingStones = new long[Sides];
    public final long[] ControlledSquares = new long[Sides];

    public Side SideToMove = TakEngine.Side.White;
    public int Turn = 0;
    public long OccupiedSquares = 0;

    public int[] AvailableCapstones;
    public int[] AvailableNormalStones;

    private final MoveGenerator _moveGenerator;

    public Board(GameConfiguration configuration) {
        Size = configuration.Size;
        SquareCount = Size * Size;

        AvailableCapstones = new int[] {
                configuration.Capstones,
                configuration.Capstones
        };
        AvailableNormalStones = new int[] {
                configuration.NormalStones,
                configuration.NormalStones
        };

        Stacks = new ArrayList[SquareCount];

        for (int square = 0; square < Stacks.length; square++) {
            Stacks[square] = new ArrayList<>();
        }
        FlatStones = new int[Sides][SquareCount];

        _moveGenerator = new MoveGenerator(this);
    }
    public Board(int size) {
        this(GameConfiguration.BySize.get(size));
    }
    
    public List<IMove> generateMoves() {
        return _moveGenerator.generateMoves();
    }

    public void makeMove(IMove move) {
        if (move instanceof Placement) {
            place((Placement) move);
        } else if (move instanceof Movement) {
            move((Movement) move);
        }
    }
    public void addStones(int square, ArrayList<Stone> stones) {
        Stacks[square].addAll(stones);
        ControlledSquares[SideToMove.ordinal()] = BitHelper.setBit(ControlledSquares[SideToMove.ordinal()], square);
        updateBitboards(square, stones.get(stones.size() - 1));
    }
    public void addStone(int square, Stone stone) {
        // NOTE: might be ineffective and need to change later
        addStones(square, new ArrayList<>(List.of(new Stone[]{stone})));
    }
    public void removeStone(int square) {
        removeStones(square, 1);
    }
    public ArrayList<Stone> removeStones(int square, int count) {
        var removedStones = Stacks[square].subList(Stacks[square].size() - count, Stacks[square].size());
        updateBitboards(square, Stacks[square].get(Stacks[square].size() - 1));
        return (ArrayList<Stone>) removedStones;
    }
    private void updateBitboards(int square, Stone stone) {
        ControlledSquares[SideToMove.ordinal()] = BitHelper.flipBit(ControlledSquares[stone.getSide().ordinal()], square);
        switch (stone.getStoneType()) {
            case FlatStone -> {
                FlatStones[SideToMove.ordinal()][square] = BitHelper.flipBit(FlatStones[stone.getSide().ordinal()][square], Stacks[square].size() - 1);
            }
            case StandingStone ->  {
                StandingStones[SideToMove.ordinal()] = BitHelper.flipBit(StandingStones[stone.getSide().ordinal()], square);
            }
            case Capstone -> {
                Capstones[SideToMove.ordinal()] = BitHelper.flipBit(Capstones[stone.getSide().ordinal()], square);
            }
        }
    }

    public void place(Placement placement) {
        addStone(placement.Square, placement.Stone);
        if (placement.Stone.getStoneType() == StoneType.Capstone) {
            AvailableCapstones[placement.Stone.getSide().ordinal()] -= 1;
        }
        else {
            AvailableNormalStones[placement.Stone.getSide().ordinal()] -= 1;
        }
    }

    public void move(Movement movement) {
        List<Stone> stonesToTake = removeStones(movement.Square, Stacks[movement.Square].size() - movement.FlatStonesToLeave.get(0));
        int squareCount = 1;
        for (int stonesToLeave : movement.FlatStonesToLeave) {
            int directionalOffset = movement.Direction.getShiftAmount(Size) * squareCount;
            int square = movement.Square + directionalOffset;
            ArrayList<Stone> stonesToAdd = (ArrayList<Stone>) stonesToTake.subList(0, stonesToLeave);
            addStones(square, stonesToAdd);
            squareCount += 1;
        }
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
            long file = Tables.getRank(Size, fileIndex);
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

    public static String bitboardToString(long bitboard, int boardSize) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < boardSize * boardSize; i++) {
            if (i % boardSize == 0) {
                string.append('\n');
            }
            
            long bit = (bitboard >> i) & 1;
            if (bit == 0) {
                string.append('0');
            }
            else {
                string.append('1');
            }
        }
        return string.toString();
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

    private StringBuilder getBitboardStringBuilder(String[] stackStrings, int[] highestFileStackSizes) {
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

