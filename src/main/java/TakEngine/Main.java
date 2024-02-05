package TakEngine;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(GameConfiguration.BySize.get(7));
        for (int i = 0; i < board.Stacks.length; i++) {
            board.Stacks[i].add(new Stone(StoneType.Capstone, Side.White));
        }
        
        board.Stacks[27].add(new Stone(StoneType.FlatStone, Side.White));
        board.Stacks[27].add(new Stone(StoneType.StandingStone, Side.White));
        System.out.println(board);
    }
}
