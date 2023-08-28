package TakEngine;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(GameConfiguration.BySize.get(7));
        board.generateMoves();
    }
}