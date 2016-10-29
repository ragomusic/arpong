package com.example.android.midiscope;

public class ArpongSequence {
    private int originalNote;
    private int originalVel;

    private int nextNote;
    private int nextVel;

    private int currentStep = 0;
    private static final int  maxStep = 8;

    ArpongSequence(int note, int vel) {
        originalNote  = nextNote = note;
        originalVel = nextVel = vel;
    }

    public int getOriginalNote() {
        return originalNote;
    }

    public int getOriginalVel() {
        return originalVel;
    }

    public int getNextNote() {
        return nextNote;
    }

    public int getNextVel() {
        return nextVel;
    }

    public void setNextNote(int midiNoteNumber) {
        nextNote = midiNoteNumber;
    }

    public void setNextVelocity(int velocity) {
        nextVel = velocity;
    }

    public void advance() {
        currentStep++;
        if (currentStep >= maxStep) {
            currentStep = 0;
        }
    }

    public int getCurrentStep() {
        return currentStep;
    }
}
