package TakEngine.Search;

import TakEngine.Moves.IMove;

public class SearchResult {
    public IMove BestMove;
    public int DepthReached;

    public SearchResult() {
        // Initialize bestMove as null, since Option<Move> best_move is None in Rust
        this.BestMove = null;
        this.DepthReached = 0;
    }
}
