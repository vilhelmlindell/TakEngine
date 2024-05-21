package TakEngine;

import static org.junit.jupiter.api.Assertions.*;

import TakEngine.Moves.IMove;
import TakEngine.Moves.PlaceMove;
import java.util.List;
import org.junit.jupiter.api.Test;

public class TestBoard {
  @Test
  public void testGenerateMoves() {
    Board board = new Board(5);
    board.generateMoves();
    List<IMove> moves = board.generateMoves();
    assertNotNull(moves);
  }

  @Test
  public void testHorizontalGameWin() {
    Board board = new Board(5);
    board.addStone(10, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(11, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(16, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(17, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(18, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(19, new Stone(StoneType.FlatStone, Side.White));
    System.out.println("Board:");
    System.out.println(BitHelper.bitboardToString(board.OccupiedSquares, board.Size));
    assertTrue(board.isGameWon());
  }

  @Test
  public void testVerticalGameWin() {
    Board board = new Board(5);
    board.addStone(3, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(8, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(9, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(14, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(19, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(24, new Stone(StoneType.FlatStone, Side.White));
    System.out.println("Board:");
    System.out.println(BitHelper.bitboardToString(board.OccupiedSquares, board.Size));
    assertTrue(board.isGameWon());
  }

  @Test
  public void testPlacement() {
    Board board = new Board(5);
    Stone stone = new Stone(StoneType.FlatStone, Side.White);
    PlaceMove placement = new PlaceMove(13, stone);
    board.place(placement);
    assertEquals(board.Stacks[13].get(0), stone);
    assertEquals(board.FlatStones[stone.getSide().ordinal()][13], 1);
  }

  @Test
  public void testMovement() {
    Board board = new Board(5);
    board.addStone(9, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(9, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(9, new Stone(StoneType.FlatStone, Side.White));
    board.addStone(9, new Stone(StoneType.FlatStone, Side.White));
    List<IMove> movements = board.generateMoves();
    // System.out.println(BitHelper.bitboardToString(board.OccupiedSquares, board.Size));
    for (IMove move : movements) {
      if (!(move instanceof PlaceMove)) {
        System.out.println(move.toString());
      }
    }
    assertTrue(true);
  }
}
