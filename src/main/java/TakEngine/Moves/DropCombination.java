package TakEngine.Moves;

import java.util.Iterator;
import java.util.List;

public class DropCombination implements Iterable<Integer> {
  // Every group of 3 bits represent the nth drop
  public final int Length;
  private int _data = 0;

  public DropCombination(List<Integer> combination) {
    Length = combination.size();
    // assert combination.length >= 1 && combination.length <= 8;
    for (int i = 0; i < combination.size(); i++) {
      int dropSize = combination.get(i);
      _data |= dropSize << (3 * i);
    }
  }

  public int get(int index) {
    return (_data >> (3 * index)) & ~(0b111);
  }

  @Override
  public Iterator<Integer> iterator() {
    return new DropIterator();
  }

  private class DropIterator implements Iterator<Integer> {
    private int _currentIndex = 0;

    @Override
    public boolean hasNext() {
      return _currentIndex < Length;
    }

    @Override
    public Integer next() {
      int dropCount = get(_currentIndex);
      _currentIndex++;
      return dropCount;
    }
  }
}
