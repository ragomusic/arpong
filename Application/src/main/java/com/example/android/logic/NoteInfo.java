package com.example.android.logic;

/**
 * Created by hlindh on 10/29/16.
 */

public class NoteInfo {
    public int degree; // row
    public int velocity;
    public int midiNoteNumber;

    public NoteInfo(int degree, int velocity, int midiNoteNumber) {
        this.degree = degree;
        this.velocity = velocity;
        this.midiNoteNumber = midiNoteNumber;
    }
}
