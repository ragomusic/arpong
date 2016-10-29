package com.example.android.midiscope;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.AsyncTask;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.android.logic.NoteInfo;
import com.example.android.logic.SequenceManager;
import java.io.IOException;
import java.util.ArrayList;

public class ArpongEngine {
    public static final String TAG = "ArpongEngine";
    private static ArpongEngine ourInstance = new ArpongEngine();

    public static ArpongEngine getInstance() {
        return ourInstance;
    }

    public static final String ARPONG_EVENT = "ArpongEvent";
    public static final String ARPONG_EVENT_TYPE = "ArpongEventType";
    public static final int APRONG_EVENT_SQUARE_ON = 1;
    public static final int APRONG_EVENT_COLLISION = 2;

    public static final String ARPONG_SQUARE_ON_ID = "id";
    public static final String ARPONG_SQUARE_ON_STEP = "step";
    public static final String ARPONG_SQUARE_ON_DEGREE = "degree";
    public static final String ARPONG_SQUARE_ON_VEL = "vel";

    private int mChannel = 5;
    private float mTempo = 120;
    private float msPerBeat = 500;
    private static final Object mTempoLock = new Object();

    private boolean mRunning = false;
    private static final Object mRunningLock = new Object();

    private SequencerTask mySequencer = null;

    Context mContext = null;
    MidiReceiver mMidiReceiver = null;

    private ArpongEngine() {
    }
    public void initMidiInput(Context context, MidiReceiver midiReceiver) {
        mContext = context;
        mMidiReceiver = midiReceiver;
        Log.i(TAG, "mMidiReceiver done " + mMidiReceiver);
    }

    private void sendNote(int channel, int note, int vel, boolean on) {
        Log.i(TAG, String.format(" play: (%d, %d) %s", note, vel, on ? "on" : "off"));
        if (mMidiReceiver != null) {
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
                mMidiReceiver.send(buffer, offset, numBytes);
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
                        ArpongSequence seq = sequences.get(i);
                        //turn off previous notes
                        int noteOff = seq.getNextNote();
                        int velOff = seq.getNextVel();

                        sendNote(mChannel, noteOff, velOff, false);

                        NoteInfo info = SequenceManager.getNoteInfoForIndex(SequenceManager.PATTERN.LOW, currentBeat, sequences.get(i).getOriginalNote());
                        seq.setNextNote(info.midiNoteNumber);
                        seq.setNextVelocity(info.velocity);

                        sequences.get(i).advance();
                        //turn on next notes
                        int noteOn = seq.getNextNote();
                        int velOn = seq.getNextVel();
                        sendNote(mChannel, noteOn, velOn, true);

                        sendEventNoteOn(i, currentBeat, noteOn, velOn);
                        //Info for the ui
                        int degree = noteOn % 16;

                        Log.i(TAG, String.format(" sequence: %d  play: (%d, %d)",i, noteOn, velOn));
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

    public void sendEventNoteOn(int id, int step, int note, int vel) {

//        ARPONG_SQUARE_ON_ID = "id";
//        public static final String ARPONG_SQUARE_ON_STEP = "step";
//        public static final String ARPONG_SQUARE_ON_DEGREE = "degree";
//        public static final String ARPONG_SQUARE_ON_VEL = "vel";

        Log.i(TAG, "sendEventNoteOn");
        if (mContext != null) {

            int degree = note % 16;
            double velocity = 2.0 * vel / 127.0;
            Intent intent = new Intent(ARPONG_EVENT);
            intent.putExtra(ARPONG_EVENT_TYPE, APRONG_EVENT_SQUARE_ON);
            intent.putExtra(ARPONG_SQUARE_ON_ID, id);
            intent.putExtra(ARPONG_SQUARE_ON_DEGREE, degree);
            intent.putExtra(ARPONG_SQUARE_ON_STEP, step);
            intent.putExtra(ARPONG_SQUARE_ON_VEL, velocity);

            Log.i(TAG, "sendEventNoteOn degree: " + degree);
            LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
        }

    }
}
