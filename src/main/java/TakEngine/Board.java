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
    public void addStone(Stone stone, int square) {
        Stacks[square].add(stone);
        ControlledSquares[SideToMove.ordinal()] = BitHelper.setBit(ControlledSquares[SideToMove.ordinal()], square);
        switch (stone.getStoneType()) {
            case FlatStone -> {
                FlatStones[SideToMove.ordinal()][square] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][square], Stacks[square].size());
                AvailableNormalStones[SideToMove.ordinal()] -= 1;
            }
            case StandingStone ->  {
                StandingStones[SideToMove.ordinal()] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][square], Stacks[square].size());
                AvailableNormalStones[SideToMove.ordinal()] -= 1;
            }
            case Capstone -> {
                Capstones[SideToMove.ordinal()] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][square], Stacks[square].size());
                AvailableCapstones[SideToMove.ordinal()] -= 1;
            }
        }
    }

    private void place(Placement placement) {
        Stacks[placement.Square].add(placement.Stone);
        ControlledSquares[SideToMove.ordinal()] = BitHelper.setBit(ControlledSquares[SideToMove.ordinal()], placement.Square);
        switch (placement.Stone.getStoneType()) {
            case FlatStone -> {
                FlatStones[SideToMove.ordinal()][placement.Square] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][placement.Square], Stacks[placement.Square].size());
                AvailableNormalStones[SideToMove.ordinal()] -= 1;
            }
            case StandingStone ->  {
                StandingStones[SideToMove.ordinal()] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][placement.Square], Stacks[placement.Square].size());
                AvailableNormalStones[SideToMove.ordinal()] -= 1;
            }
            case Capstone -> {
                Capstones[SideToMove.ordinal()] = BitHelper.setBit(FlatStones[SideToMove.ordinal()][placement.Square], Stacks[placement.Square].size());
                AvailableCapstones[SideToMove.ordinal()] -= 1;
            }
        }
    }

    private void move(Movement movement) {
        int index = 1;
        //for (int stonesToLeave : movement.FlatStonesToLeave) {
        //    int stonesToTake = Stacks[movement.Square].size() - stonesToLeave;
        //    int stonesToTakeBits = ((1 << stonesToTake) - 1) << stonesToLeave;
        //    int nextSquare = movement.Square + movement.Direction.getShiftAmount(Size) * index;
        //    for (Side side : TakEngine.Side.values()) {
        //        nextSquarestonesToTakeBits &= FlatStones[Side.ordinal()][movement.Square] << stonesToLeave;
        //    }
        //    index += 1;
        //}
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

        return result.toString();
    }
}

