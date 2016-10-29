package com.example.android.logic;

import android.graphics.RectF;

/**
 * Created by hlindh on 10/29/16.
 */

public class NoteSequence {
    private RectF position;
    private int[] pitchSet;
    private int[] sequence;

    public RectF getPosition() {
        return position;
    }

    public void setPosition(RectF position) {
        this.position = position;
    }
}
