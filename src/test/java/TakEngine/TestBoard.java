package TakEngine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import TakEngine.Moves.*;;

public class TestBoard {
    @Test
    public void testGenerateMoves() {
        Board board = new Board(5);
        board.generateMoves();
        List<IMove> moves = board.generateMoves();
        assertNotNull(moves);
    }

    @Test
    public void testGameWin() {
        Board board = new Board(5);
        board.addStone(10, new Stone(StoneType.FlatStone, Side.White));
        board.addStone(11, new Stone(StoneType.FlatStone, Side.White));
        board.addStone(12, new Stone(StoneType.FlatStone, Side.White));
        board.addStone(13, new Stone(StoneType.FlatStone, Side.White));
        board.addStone(14, new Stone(StoneType.FlatStone, Side.White));
        System.out.println("Board:");
        System.out.println(Board.bitboardToString(board.OccupiedSquares, board.Size));
        assertTrue(board.isGameWon());
    }
    
    @Test
    public void testPlacement() {
        Board board = new Board(5);
        Stone stone = new Stone(StoneType.FlatStone, Side.White);
        Placement placement = new Placement(13, stone);
        board.place(placement);
        assertEquals(board.Stacks[13].get(0), stone);
        assertEquals(board.FlatStones[stone.getSide().ordinal()][13], 1);
        assertEquals(board.);
    }
    @Test
    public void testMovement() {
    }
}
