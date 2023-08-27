package TakEngine;

public final class Stone {
    private final byte _data;

    public Stone(StoneType stoneType, Side side) {
        _data = (byte) (((byte) stoneType.ordinal() << 1) | (byte) side.ordinal());
    }

    public StoneType getStoneType() {
        return StoneType.values()[_data >> 1];
    }

    public Side getSide() {
        return Side.values()[_data & 1];
    }
}
