package com.example.android.midiscope;

public class ArpongSequence {
    private int originalNote;
    private int originalVel;

    private int nextNote;
    private int nextVel;

    private int currentStep = 0;
    private static final int  maxStep = 8;

    ArpongSequence(int note, int vel) {
        originalNote  = note;
        originalVel = vel;

        computeNextNotes();
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

    public void advance() {
        currentStep++;
        if (currentStep >= maxStep) {
            currentStep = 0;
        }

       computeNextNotes();
    }

    private void computeNextNotes() {
        //compue next notes
        nextNote = originalNote;
        nextVel = originalVel;
    }

    public int getCurrentStep() {
        return currentStep;
    }
}
