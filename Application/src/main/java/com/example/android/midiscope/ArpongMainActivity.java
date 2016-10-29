package com.example.android.midiscope;


import android.app.ActionBar;
import android.app.Activity;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.android.common.midi.MidiFramer;
import com.example.android.common.midi.MidiInputPortSelector;
import com.example.android.common.midi.MidiOutputPortSelector;
import com.example.android.common.midi.MidiPortWrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ArpongMainActivity extends Activity implements ScopeLogger {

    private static final int MAX_LINES = 100;

    private final LinkedList<String> mLogLines = new LinkedList<>();
    private TextView mLog;
    private ScrollView mScroller;
    private MidiOutputPortSelector mLogSenderSelector;
    private MidiInputPortSelector mLogReceiverSelector;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arpong_main);

        setActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        mLog = (TextView) findViewById(R.id.log);
        mScroller = (ScrollView) findViewById(R.id.scroll);

        // Setup MIDI
        MidiManager midiManager = (MidiManager) getSystemService(MIDI_SERVICE);

        // Receiver that prints the messages.
        MidiReceiver loggingReceiver = new LoggingReceiver(this);
        //MidiReceiver myReceiver = new ArpongMidiReceiver(this);

        // Receiver that parses raw data into complete messages.
        MidiFramer connectFramer = new MidiFramer(loggingReceiver);

        // Setup a menu to select an input source.
        mLogSenderSelector = new MidiOutputPortSelector(midiManager, this, R.id.spinner_senders) {
            @Override
            public void onPortSelected(final MidiPortWrapper wrapper) {
                super.onPortSelected(wrapper);
                if (wrapper != null) {
                    mLogLines.clear();
                    MidiDeviceInfo deviceInfo = wrapper.getDeviceInfo();
                    if (deviceInfo == null) {
                        log(getString(R.string.header_text));
                    } else {
                        log(MidiPrinter.formatDeviceInfo(deviceInfo));
                    }
                }
            }
        };
        mLogSenderSelector.getSender().connect(connectFramer);

        mLogReceiverSelector = new MidiInputPortSelector(midiManager, this, R.id.spinner_receivers) {
            @Override
            public void onPortSelected(final MidiPortWrapper wrapper) {
                super.onPortSelected(wrapper);
                if (wrapper != null) {
                    mLogLines.clear();
                    MidiDeviceInfo deviceInfo = wrapper.getDeviceInfo();
                    if (deviceInfo == null) {
                        log(getString(R.string.header_text));
                    } else {
                        log(MidiPrinter.formatDeviceInfo(deviceInfo));
                    }

                    MidiInputPort input = (MidiInputPort) mLogReceiverSelector.getReceiver();  //input port!!
                    ArpongEngine.getInstance().initMidiInput(input);
                }
            }
        };


        // Tell the virtual device to log its messages here..
        MidiScope.setScopeLogger(this);

//        ArpongEngine.getInstance().initMidiOutput(getApplicationContext());
        ArpongEngine.getInstance().setTempo(120);
        ArpongEngine.getInstance().start(); //start ArpongEngine
    }

    @Override
    public void onDestroy() {
        mLogSenderSelector.onClose();
        // The scope will live on as a service so we need to tell it to stop
        // writing log messages to this Activity.
        MidiScope.setScopeLogger(null);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setKeepScreenOn(menu.findItem(R.id.action_keep_screen_on).isChecked());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_all:
                mLogLines.clear();
                logOnUiThread("");
                break;
            case R.id.action_keep_screen_on:
                boolean checked = !item.isChecked();
                setKeepScreenOn(checked);
                item.setChecked(checked);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setKeepScreenOn(boolean keepScreenOn) {
        if (keepScreenOn) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    public void log(final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logOnUiThread(string);
            }
        });
    }

    /**
     * Logs a message to our TextView. This needs to be called from the UI thread.
     */
    private void logOnUiThread(String s) {
        mLogLines.add(s);
        if (mLogLines.size() > MAX_LINES) {
            mLogLines.removeFirst();
        }
        // Render line buffer to one String.
        StringBuilder sb = new StringBuilder();
        for (String line : mLogLines) {
            sb.append(line).append('\n');
        }
        mLog.setText(sb.toString());
        mScroller.fullScroll(View.FOCUS_DOWN);
    }

    private class ArpongMidiReceiver extends MidiReceiver {
        public static final String TAG = "ArpongMidiReceiver";
        private final long NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1);
        private long mStartTime;
        private ScopeLogger mLogger;

        public ArpongMidiReceiver(ScopeLogger logger) {
            mStartTime = System.nanoTime();
            mLogger = logger;
        }

        /*
         * @see android.media.midi.MidiReceiver#onReceive(byte[], int, int, long)
         */
        @Override
        public void onSend(byte[] data, int offset, int count, long timestamp)
                throws IOException {
            StringBuilder sb = new StringBuilder();
            if (timestamp == 0) {
                sb.append(String.format("-----0----: "));
            } else {
                long monoTime = timestamp - mStartTime;
                double seconds = (double) monoTime / NANOS_PER_SECOND;
                sb.append(String.format("%10.3f: ", seconds));
            }


            {
                if (count - offset >=2) {
                    Log.i(TAG,String.format("22rago: event: %02X  offset: %d count %d", data[offset], offset, count));
                    if ((data[offset] & 0xF0)== 0x90 ) {
                        Log.i(TAG, String.format("22rago: note on: %02X ", data[offset]));

                    }
                }
            }

            sb.append(MidiPrinter.formatBytes(data, offset, count));
            sb.append(": ");
            sb.append(MidiPrinter.formatMessage(data, offset, count));
            String text = sb.toString();
            mLogger.log(text);
            Log.i(TAG, text);
        }
    }

}
