package com.example.android.logic;

import android.support.v4.util.Pair;

/**
 * Created by hlindh on 10/29/16.
 */

public class SequenceManager {

    public enum PATTERN {
        LOW,
        MEDIUM,
        HIGH
    }

    private static final int[] PATTERN_LOW_SCALE_DEGREE = { 0, 7, 11, 6, 2, 7, 13, 8 };
    private static final int[] PATTERN_LOW_SCALE_VELOCITIES = { 20, 35, 50, 65, 80, 95, 110, 127 };
    private static final int[] PATTERN_MEDIUM_SCALE_DEGREE = { 8, 7, 10, 0, 14, 5, 9, 3 };
    private static final int[] PATTERN_MEDIUM_SCALE_VELOCITIES = { 70, 80, 90, 100, 120, 100, 80, 60 };
    private static final int[] PATTERN_HIGH_SCALE_DEGREE = { 15, 9, 3, 10, 6, 5, 3, 12 };
    private static final int[] PATTERN_HIGH_SCALE_VELOCITIES = { 127, 110, 95, 80, 65, 50, 35, 20 };

    public static NoteInfo getNoteInfoForIndex(PATTERN pattern, int index, int originalNote, int harmony) {
        int[] degreeArray;
        int[] velocitiesArray;
        switch (pattern) {
            case LOW:
                degreeArray = PATTERN_LOW_SCALE_DEGREE;
                velocitiesArray = PATTERN_LOW_SCALE_VELOCITIES;
                break;
            case MEDIUM:
                degreeArray = PATTERN_MEDIUM_SCALE_DEGREE;
                velocitiesArray = PATTERN_MEDIUM_SCALE_VELOCITIES;
                break;
            case HIGH:
            default:
                degreeArray = PATTERN_HIGH_SCALE_DEGREE;
                velocitiesArray = PATTERN_HIGH_SCALE_VELOCITIES;
                break;
        }

        return new NoteInfo(degreeArray[index],
                velocitiesArray[index],
                HarmoniesManager.getMidiNoteNumber(HarmoniesManager.getHarmonyFromIndex(harmony), degreeArray[index], originalNote));
    }

    public static PATTERN getPattern(int velocity) {
        if (velocity <= 43) {
            return PATTERN.LOW;
        }

        if (velocity <= 86) {
            return PATTERN.MEDIUM;
        }

        return PATTERN.HIGH;
    }
}
