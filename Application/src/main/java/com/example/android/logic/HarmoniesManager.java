package com.example.android.logic;

/**
 * Created by hlindh on 10/28/16.
 */

public class HarmoniesManager {

    enum HARMONY {
        CMAJOR,
        CMM7M9
    }

//    public int getCorrectNote(HARMONY harmony, int value) {
//        switch (harmony) {
//            case CMAJOR:
//                break;
//            case CMM7M9:
//            default:
//                break;
//        }
//    }

    private int getClosestFromHarmony(int[] harmonyArray, int value) {
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

    private int getClosestIndex(int[] harmonyArray, int value) {
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

    public int[] getPitchSet(HARMONY harmony, int noteValue) {
        int first, last, size, array[];
        switch (harmony) {
            case CMAJOR:
                array = C_MAJOR;
                break;
            default:
            case CMM7M9:
                array = CMM7M9;
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

    private static final int[] C_MAJOR = {0, 2, 4
            , 6
            , 7
            , 9
            , 11
            , 12
            , 14
            , 16
            , 18
            , 19
            , 21
            , 23
            , 24
            , 26
            , 28
            , 30
            , 31
            , 33
            , 35
            , 36
            , 38
            , 40
            , 42
            , 43
            , 45
            , 47
            , 48
            , 50
            , 52
            , 54
            , 55
            , 57
            , 59
            , 60
            , 62
            , 64
            , 66
            , 67
            , 69
            , 71
            , 72
            , 74
            , 76
            , 78
            , 79
            , 81
            , 83
            , 84
            , 86
            , 88
            , 90
            , 91
            , 93
            , 95
            , 96
            , 98
            , 100
            , 102
            , 103
            , 105
            , 107
            , 108
            , 110
            , 112
            , 114
            , 115
            , 117
            , 119
            , 120
            , 122
            , 124
            , 126
            , 127};

    private static final int[] CMM7M9 = { 0
            ,2
            ,3
            ,7
            ,11
            ,12
            ,14
            ,15
            ,19
            ,23
            ,24
            ,26
            ,27
            ,31
            ,35
            ,36
            ,38
            ,39
            ,43
            ,47
            ,48
            ,50
            ,51
            ,55
            ,59
            ,60
            ,62
            ,63
            ,67
            ,71
            ,72
            ,74
            ,75
            ,79
            ,83
            ,84
            ,86
            ,87
            ,91
            ,95
            ,96
            ,98
            ,99
            ,103
            ,107
            ,108
            ,110
            ,111
            ,115
            ,119
            ,120
            ,122
            ,123
            ,127 };
}
