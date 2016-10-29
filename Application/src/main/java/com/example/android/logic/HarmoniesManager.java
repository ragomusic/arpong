package com.example.android.logic;

/**
 * Created by hlindh on 10/28/16.
 */

public class HarmoniesManager {

    enum HARMONY {
        HARMONY_ZERO,
        HARMONY_ONE,
        HARMONY_TWO,
        HARMONY_THREE,
        HARMONY_FOUR,
        HARMONY_FIVE,
        HARMONY_SIX,
        HARMONY_SEVEN,
        HARMONY_EIGHT,
        HARMONY_NINE,
        HARMONY_TEN,
        HARMONY_ELEVEN,
    }

    private static final int[] HARMONY_ZERO = {0, 2, 4, 7, 9, 12, 14, 16, 19, 21, 24, 26, 28, 31, 33, 36, 38, 40, 43, 45, 48, 50, 52, 55, 57, 60, 62, 64, 67, 69, 72, 74, 76, 79, 81, 84, 86, 88, 91, 93, 96, 98, 100, 103, 105, 108, 110, 112, 115, 117, 120, 122, 124, 127};
    private static final int[] HARMONY_ONE = {2, 4, 7, 9, 11, 14, 16, 19, 21, 23, 26, 28, 31, 33, 35, 38, 40, 43, 45, 47, 50, 52, 55, 57, 59, 62, 64, 67, 69, 71, 74, 76, 79, 81, 83, 86, 88, 91, 93, 95, 98, 100, 103, 105, 107, 110, 112, 115, 117, 119, 122, 124, 127};
    private static final int[] HARMONY_TWO = {2, 4, 6, 9, 11, 14, 16, 18, 21, 23, 26, 28, 30, 33, 35, 38, 40, 42, 45, 47, 50, 52, 54, 57, 59, 62, 64, 66, 69, 71, 74, 76, 78, 81, 83, 86, 88, 90, 93, 95, 98, 100, 102, 105, 107, 110, 112, 114, 117, 119, 122, 124, 126};
    private static final int[] HARMONY_THREE = {1, 4, 6, 9, 11, 13, 16, 18, 21, 23, 25, 28, 30, 33, 35, 37, 40, 42, 45, 47, 49, 52, 54, 57, 59, 61, 64, 66, 69, 71, 73, 76, 78, 81, 83, 85, 88, 90, 93, 95, 97, 100, 102, 105, 107, 109, 112, 114, 117, 119, 121, 124, 126};
    private static final int[] HARMONY_FOUR = {1, 4, 6, 8, 11, 13, 16, 18, 20, 23, 25, 28, 30, 32, 35, 37, 40, 42, 44, 47, 49, 52, 54, 56, 59, 61, 64, 66, 68, 71, 73, 76, 78, 80, 83, 85, 88, 90, 92, 95, 97, 100, 102, 104, 107, 109, 112, 114, 116, 119, 121, 124, 126};
    private static final int[] HARMONY_FIVE = {1, 3, 6, 8, 11, 13, 15, 18, 20, 23, 25, 27, 30, 32, 35, 37, 39, 42, 44, 47, 49, 51, 54, 56, 59, 61, 63, 66, 68, 71, 73, 75, 78, 80, 83, 85, 87, 90, 92, 95, 97, 99, 102, 104, 107, 109, 111, 114, 116, 119, 121, 123, 126};
    private static final int[] HARMONY_SIX = {1, 3, 6, 8, 10, 13, 15, 18, 20, 22, 25, 27, 30, 32, 34, 37, 39, 42, 44, 46, 49, 51, 54, 56, 58, 61, 63, 66, 68, 70, 73, 75, 78, 80, 82, 85, 87, 90, 92, 94, 97, 99, 102, 104, 106, 109, 111, 114, 116, 118, 121, 123, 126};
    private static final int[] HARMONY_SEVEN = {1, 3, 5, 8, 10, 13, 15, 17, 20, 22, 25, 27, 29, 32, 34, 37, 39, 41, 44, 46, 49, 51, 53, 56, 58, 61, 63, 65, 68, 70, 73, 75, 77, 80, 82, 85, 87, 89, 92, 94, 97, 99, 101, 104, 106, 109, 111, 113, 116, 118, 121, 123, 125};
    private static final int[] HARMONY_EIGHT = {0, 3, 5, 8, 10, 12, 15, 17, 20, 22, 24, 27, 29, 32, 34, 36, 39, 41, 44, 46, 48, 51, 53, 56, 58, 60, 63, 65, 68, 70, 72, 75, 77, 80, 82, 84, 87, 89, 92, 94, 96, 99, 101, 104, 106, 108, 111, 113, 116, 118, 120, 123, 125};
    private static final int[] HARMONY_NINE = {0, 3, 5, 7, 10, 12, 15, 17, 19, 22, 24, 27, 29, 31, 34, 36, 39, 41, 43, 46, 48, 51, 53, 55, 58, 60, 63, 65, 67, 70, 72, 75, 77, 79, 82, 84, 87, 89, 91, 94, 96, 99, 101, 103, 106, 108, 111, 113, 115, 118, 120, 123, 125, 127};
    private static final int[] HARMONY_TEN = {0, 2, 5, 7, 10, 12, 14, 17, 19, 22, 24, 26, 29, 31, 34, 36, 38, 41, 43, 46, 48, 50, 53, 55, 58, 60, 62, 65, 67, 70, 72, 74, 77, 79, 82, 84, 86, 89, 91, 94, 96, 98, 101, 103, 106, 108, 110, 113, 115, 118, 120, 122, 125, 127};
    private static final int[] HARMONY_ELEVEN = {0, 2, 5, 7, 9, 12, 14, 17, 19, 21, 24, 26, 29, 31, 33, 36, 38, 41, 43, 45, 48, 50, 53, 55, 57, 60, 62, 65, 67, 69, 72, 74, 77, 79, 81, 84, 86, 89, 91, 93, 96, 98, 101, 103, 105, 108, 110, 113, 115, 117, 120, 122, 125, 127};

    public static int getMidiNoteNumber(HARMONY harmony, int degree, int noteValue) {
        return getPitchSet(harmony, noteValue)[degree];
    }

    public static HARMONY getInitialHarmony() {
        return HarmoniesManager.HARMONY.HARMONY_ZERO;
    }

    private static int getClosestFromHarmony(int[] harmonyArray, int value) {
        int legalValue = -1;
        for (int i = 0; i < harmonyArray.length; i++) {
            legalValue = harmonyArray[i];
            // TODO find closest value
            if (value <= legalValue) {
                return legalValue;
            }
        }
        return legalValue;
    }

    private static int getClosestIndex(int[] harmonyArray, int value) {
        int legalValue = -1;
        for (int i = 0; i < harmonyArray.length; i++) {
            legalValue = harmonyArray[i];
            // TODO find closest value
            if (value <= legalValue) {
                return i;
            }
        }
        return legalValue;
    }

    public static int[] getPitchSet(HARMONY harmony, int noteValue) {
        int first, last, size, array[];
        switch (harmony) {
            case HARMONY_ZERO:
                array = HARMONY_ZERO;
                break;
            case HARMONY_ONE:
                array = HARMONY_ONE;
                break;
            case HARMONY_TWO:
                array = HARMONY_TWO;
                break;
            case HARMONY_THREE:
                array = HARMONY_THREE;
                break;
            case HARMONY_FOUR:
                array = HARMONY_FOUR;
                break;
            case HARMONY_FIVE:
                array = HARMONY_FIVE;
                break;
            case HARMONY_SIX:
                array = HARMONY_SIX;
                break;
            case HARMONY_SEVEN:
                array = HARMONY_SEVEN;
                break;
            case HARMONY_EIGHT:
                array = HARMONY_EIGHT;
                break;
            case HARMONY_NINE:
                array = HARMONY_NINE;
                break;
            case HARMONY_TEN:
                array = HARMONY_TEN;
                break;
            case HARMONY_ELEVEN:
            default:
                array = HARMONY_ELEVEN;
                break;
        }

        int i = getClosestIndex(array, noteValue);
        size = array.length;
        if (i >= (size - 8)) {
            last = (size - 1);
            first = last - 15;
        } else if (i <= 7) {
            first = 0;
            last = 15;
        } else {
            first = i - 8;
            last = i + 7;
        }

        int[] newArray = new int[16];
        for (int j = 0; j < newArray.length; j++) {
            newArray[j] = array[first + j];
        }

        return newArray;
    }
}
