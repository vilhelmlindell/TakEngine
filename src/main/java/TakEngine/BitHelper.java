package TakEngine;

import java.util.function.IntConsumer;


public class BitHelper {
    public static void iterateBits(long value, int boardSize, boolean reverse, IntConsumer consumer) {
        while (value != 0) {
            int square;
            if (reverse) {
                square = 64 - Long.numberOfLeadingZeros(value);
                value ^= Long.highestOneBit(value);
            } else {
                square = Long.numberOfTrailingZeros(value);
                value ^= Long.lowestOneBit(value);
            }
            consumer.accept(square);
        }
    }

    public static long shiftBits(long value, int boardSize, Direction direction) {
        value ^= Tables.getBoundaryLine(boardSize, direction);
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
    public static long clearBit(long value, int bitIndex) {
        return value & (1L << bitIndex);
    }
    public static int setBit(int value, int bitIndex) {
        return value | (1 << bitIndex);
    }
    public static long flipBit(long value, int bitIndex) {
        return value ^ (1L << bitIndex);
    }
    public static int flipBit(int value, int bitIndex) {
        return value ^ (1 << bitIndex);
    }

    public static String bitboardToString(long bitboard, int boardSize) {
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < boardSize * boardSize; i++) {
            if (i % boardSize == 0) {
                string.append('\n');
            }

            long bit = (bitboard >> i) & 1;
            if (bit == 0) {
                string.append('0');
            }
            else {
                string.append('1');
            }
        }
        return string.toString();
    }
}
