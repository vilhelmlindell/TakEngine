package TakEngine;

import java.util.Map;

public final class GameConfiguration {
    public final int Size;
    public final int NormalStones;
    public final int Capstones;

    public static final Map<Integer, GameConfiguration> BySize = Map.of(
            3, new GameConfiguration(3, 10, 0),
            4, new GameConfiguration(4, 15, 0),
            5, new GameConfiguration(5, 21, 1),
            6, new GameConfiguration(6, 30, 1),
            7, new GameConfiguration(7, 40, 2),
            8, new GameConfiguration(8, 50, 2)
    );

    public GameConfiguration(int size, int normalStones, int capstones) {
        Size = size;
        NormalStones = normalStones;
        Capstones = capstones;
    }
}
