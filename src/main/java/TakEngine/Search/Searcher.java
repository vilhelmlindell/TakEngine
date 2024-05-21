package TakEngine.Search;

import TakEngine.Board;
import TakEngine.Moves.IMove;
import java.util.List;

public class Searcher {
  private final Board _board;

  public Searcher(Board board) {
    _board = board;
  }

  public SearchResult Search(float maxTime) {
    float start = System.currentTimeMillis();
    SearchResult result = new SearchResult();
    int depth = 1;
    while (System.currentTimeMillis() - start > maxTime) {
      float lowestEval = Float.NEGATIVE_INFINITY;
      List<IMove> moves = _board.MoveGenerator.generateMoves();
      for (IMove move : moves) {
        if (System.currentTimeMillis() - start > maxTime) {
          result.DepthReached = depth - 1;
          return result;
        }

        _board.makeMove(move);
        float eval = NegaMax(0, Integer.MIN_VALUE + 1, Integer.MAX_VALUE, 0);
        _board.unmakeMove(move);

        if (eval > lowestEval) {
          result.BestMove = move;
        }
      }
      depth++;
    }
    return result;
  }

  private float NegaMax(int depth, int alpha, int beta, int ply) {
    if (depth == 0) {
      // let eval = _board.
    }

    List<IMove> moves = _board.MoveGenerator.generateMoves();

    for (IMove move : moves) {
      _board.makeMove(move);
      float eval = -NegaMax(depth - 1, -beta, -alpha, ply + 1);
      _board.unmakeMove(move);
    }
    return 0;
  }
}
