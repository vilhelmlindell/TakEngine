import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import TakEngine.*;
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
        board.Stacks[10].add(new Stone(StoneType.FlatStone, Side.White));
        board.Stacks[11].add(new Stone(StoneType.FlatStone, Side.White));
        board.Stacks[12].add(new Stone(StoneType.FlatStone, Side.White));
        board.Stacks[13].add(new Stone(StoneType.FlatStone, Side.White));
        board.Stacks[14].add(new Stone(StoneType.FlatStone, Side.White));
        assertEquals(board.isGameWon(), true);
    }
}
