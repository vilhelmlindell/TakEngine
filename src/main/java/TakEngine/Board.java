package TakEngine;

import TakEngine.Moves.IMove;
import TakEngine.Moves.MoveGenerator;
import TakEngine.Moves.Movement;
import TakEngine.Moves.Placement;

import java.util.ArrayList;

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
        long controlledSquares = ControlledSquares[Side.ordinal()];
        while (controlledSquares != 0) {
            int square = Long.numberOfLeadingZeros(controlledSquares);
            controlledSquares &= ~(1L << square);

            // Stone is on left edge
            if (square % Size == 0) {
            }
            // Stone is on right edge
            else if (square % Size == Size - 1) {
            }

            // Stone is on top edge
            if (square / Size == 0) {
            }
            // Stone is on bottom edge
            else if (square / Size == Size - 1) {
            }
        }
        return false;
    }
}

