package com.example.android.midiscope;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

public class ArpongEngine {
    public static final String TAG = "ArpongEngine";
    private static ArpongEngine ourInstance = new ArpongEngine();

    public static ArpongEngine getInstance() {
        return ourInstance;
    }

    private int mChannel = 5;
    private float mTempo = 120;
    private float msPerBeat = 500;
    private static final Object mTempoLock = new Object();

    private boolean mRunning = false;
    private static final Object mRunningLock = new Object();

    private SequencerTask mySequencer = null;

    Context mContext = null;
    MidiManager mMidiManager = null;
    MidiInputPort mMidiInput = null;

    private ArpongEngine() {
    }
    public void initMidiInput(MidiInputPort input) {
        mMidiInput = input;
        Log.i(TAG, "initMidiOutput done");
    }

    private void sendNote(int channel, int note, int vel, boolean on) {
        Log.i(TAG, String.format(" play: (%d, %d) %s", note, vel, on ? "on" : "off"));
        if (mMidiInput != null) {
            byte prefix = on ? (byte)0x90 : (byte)0x80;
            byte[] buffer = new byte[32];
            int numBytes = 0;
          //  int channel = mChannel; // MIDI channels 1-16 are encoded as 0-15.
            buffer[numBytes++] = (byte) (prefix + (channel - 1)); // note on
            buffer[numBytes++] = (byte) note; // pitch is middle C
            buffer[numBytes++] = (byte) vel; // max velocity
            int offset = 0;
// post is non-blocking
            try {
                mMidiInput.send(buffer, offset, numBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "midi input not set");
        }
    }

    public void startNote(int note, int velocity) {
//        Log.i(TAG, String.format("Arpong: noteOn: %d, %d", note, velocity));
        if (mySequencer != null) {
            mySequencer.startNote(note, velocity);
        }
    }


    public void stopNote(int note, int velocity) {
//        Log.i(TAG, String.format("Arpong: noteOff: %d, %d", note, velocity));
        if (mySequencer != null) {
            mySequencer.stopNote(note, velocity);
        }
    }


    public void setTempo(float tempo) {
        synchronized (mTempoLock) {
            if (tempo>10.0 && tempo <400) {
                mTempo = tempo;
                msPerBeat = 60000 / tempo;
            }
        }
    }

    public float getTempo() {
        synchronized (mTempoLock) {
            return mTempo;
        }
    }

    public float getMsPerBeat() {
        synchronized (mTempoLock) {
            return msPerBeat;
        }
    }

    private boolean getRunning() {
        synchronized (mRunningLock) {
            return mRunning;
        }
    }

    public void start() {
        synchronized (mRunningLock) {
            mRunning = true;
        }
        if(mySequencer == null) {
            mySequencer = new SequencerTask() {
            };

            mySequencer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
        }
//        new SequencerTask() {}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
    }

    public void stop() {
        synchronized (mRunningLock) {
            mRunning = false;
        }

        mySequencer = null;
    }

    private abstract class SequencerTask extends AsyncTask<String, String, String> {
        boolean running = true;

        private long timePrevBeat = 0;
        private int currentBeat = 0;
        private int maxBeat = 8;

        private ArrayList<ArpongSequence> sequences = new ArrayList<ArpongSequence>();


        protected SequencerTask() {
            Log.i(TAG,"Started sequencer with tempo:" + getTempo());

        }

        protected String doInBackground(String... params) {

            while (running) {

                float msPerBeat = getMsPerBeat();
                long currTime = System.currentTimeMillis();


                if (timePrevBeat == 0 || (currTime - timePrevBeat) > msPerBeat) {
                    int activeSequences = sequences.size();
//                    Log.i(TAG, String.format(" [%d] diff: %d  seq: %d", currentBeat,
//                            (currTime - timePrevBeat), activeSequences));
                    timePrevBeat = currTime;
                    currentBeat++;
                    if(currentBeat >= maxBeat) {
                        currentBeat = 0; //wrap around
                    }

                    //Get queued notes to turn on and off.
                    for (int i = 0; i< sequences.size(); i++) {
                        ArpongSequence seq =sequences.get(i);
                        //turn off previous notes
                        int noteOff = seq.getNextNote();
                        int velOff = seq.getNextVel();

                        sendNote(mChannel, noteOff, velOff, false);

                        sequences.get(i).advance();
                        //turn on next notes
                        int noteOn = seq.getNextNote();
                        int velOn = seq.getNextVel();
                        sendNote(mChannel, noteOn, velOn, true);


                        //Log.i(TAG, String.format(" sequence: %d  play: (%d, %d)",i, note, vel));
                    }


                }


                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                running = getRunning();
            }
            String result = "done";
            return result;
        }

        protected void onPostExecute(String result) {

        }

        public void startNote(int note, int velocity) {
            Log.i(TAG, String.format("Sequencer: noteOn: %d, %d", note, velocity));
            //start new sequence...
            sequences.add( new ArpongSequence(note, velocity));
        }


        public void stopNote(int note, int velocity) {
            Log.i(TAG, String.format("Sequencer: noteOff: %d, %d", note, velocity));
            //find element to remove
            for (int i = sequences.size()-1; i>=0; i--) {
                if (sequences.get(i).getOriginalNote() == note ) {
                    //remove this
                    int noteOff = sequences.get(i).getNextNote();
                    int velOff = sequences.get(i).getNextVel();

                    sendNote(mChannel, noteOff, velOff, false);
                    sequences.remove(i);
                }
            }

            //stop sequence.
        }

    }

}
