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
    public final ArrayList<Stone>[] Stones;
    public final int[][] FlatStonesBySide;
    public final long[] Capstones = new long[Sides];
    public final long[] StandingStones = new long[Sides];
    public final long[] ControlledSquares = new long[Sides];

    public Side Side = TakEngine.Side.White;
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

        Stones = new ArrayList[SquareCount];
        FlatStonesBySide = new int[Sides][SquareCount];

        _moveGenerator = new MoveGenerator(this);
    }
    
    public void generateMoves() {
        List<IMove> moves = _moveGenerator.generateMoves();
        System.out.println(moves.size());
    }

    public void makeMove(IMove move) {
        if (move instanceof Placement) {
            place((Placement) move);
        } else if (move instanceof Movement) {
            move((Movement) move);
        }
    }

    private void place(Placement placement) {
    }

    private void move(Movement movement) {
    }

    public boolean isGameWon() {
        long controlledSquares = ControlledSquares[Side.ordinal()] ^ StandingStones[Side.ordinal()];
        
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
}

