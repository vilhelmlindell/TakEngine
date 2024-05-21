package TakEngine;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Stone stone = (Stone) o;
    return _data == stone._data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(_data);
  }
}
