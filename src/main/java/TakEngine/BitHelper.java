package TakEngine;

import java.util.function.IntConsumer;


public class BitHelper {
    public static void iterateBits(long value, boolean reverse, IntConsumer consumer) {
        while (value != 0) {
            if (reverse) {
                consumer.accept(Long.numberOfLeadingZeros(value));
            } else {
                consumer.accept(Long.numberOfTrailingZeros(value));
            }
            value ^= Long.lowestOneBit(value);
        }
    }

    public static long shiftBits(long value, int boardSize, Direction direction) {
        value ^= Tables.getBoundaryLineBits(boardSize, direction);
        int shiftAmount = direction.getShiftAmount(boardSize);
        if (direction.getShiftAmount(boardSize) < 0) {
            return value << Math.abs(shiftAmount);
        } else {
            return value >> shiftAmount;
        }
    }

    public static long setBit(long value, int bitIndex) {
        return value | (1L << bitIndex);
    }
}
