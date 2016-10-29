package com.example.android.midiscope;


import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.media.midi.MidiDeviceInfo;
import android.media.midi.MidiInputPort;
import android.media.midi.MidiManager;
import android.media.midi.MidiReceiver;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;
import android.webkit.JavascriptInterface;


import com.example.android.common.midi.MidiFramer;
import com.example.android.common.midi.MidiInputPortSelector;
import com.example.android.common.midi.MidiOutputPortSelector;
import com.example.android.common.midi.MidiPortWrapper;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

public class ArpongMainActivity extends Activity implements ScopeLogger {
    public static final String TAG = "ArpongMainActivity";

    private static final int MAX_LINES = 100;

    private final LinkedList<String> mLogLines = new LinkedList<>();
    private TextView mLog;
    private ScrollView mScroller;
    private MidiOutputPortSelector mLogSenderSelector;

    private MidiInputPortSelector mLogReceiverSelector;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

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
        final MidiManager midiManager = (MidiManager) getSystemService(MIDI_SERVICE);

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

        mLogReceiverSelector = new MidiInputPortSelector(midiManager, this, R.id.spinner_receivers);

        MidiReceiver proxyReceiver = new MidiReceiver() {
            @Override
            public void onSend(byte[] bytes, int i, int i1, long l) throws IOException {
                MidiReceiver r = mLogReceiverSelector.getReceiver();
                if (r != null) {
                    r.send(bytes, i , i1, l);
                }
            }
        };

        ArpongEngine.getInstance().initMidiInput(this, proxyReceiver);

        // Tell the virtual device to log its messages here..
        MidiScope.setScopeLogger(this);

//        ArpongEngine.getInstance().initMidiOutput(getApplicationContext());
        ArpongEngine.getInstance().setTempo(120);
        ArpongEngine.getInstance().start(); //start ArpongEngine
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // This part adds the webview created by Toshi.
        WebView w = (WebView) findViewById(R.id.web);
        Uri uri = Uri.parse("http://sainome.com/arpong_webview/src/index2.html");
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setLoadWithOverviewMode(true);
        w.getSettings().setUseWideViewPort(true);
        w.getSettings().setBuiltInZoomControls(true);
        w.getSettings().setDisplayZoomControls(false);
        w.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        w.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);

        w.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        w.loadUrl("http://sainome.com/arpong_webview/src/index_noseq.html");
        w.addJavascriptInterface(new JsInterface(), "AndroidApp");
        w.loadUrl("javascript:changeBackgroundColor()");

        LocalBroadcastManager.getInstance(this).registerReceiver(mBReceiver, new IntentFilter(ArpongEngine.ARPONG_EVENT));
    }

    public class JsInterface {
        @JavascriptInterface
        void receiveString(String value) {
            // String received from WebView
            Log.d("MyApp", value);
        }
    }



    @Override
    public void onDestroy() {
        mLogSenderSelector.onClose();
        mLogReceiverSelector.onClose();
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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("ArpongMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        LocalBroadcastManager.getInstance(this).registerReceiver(mBReceiver, new IntentFilter(ArpongEngine.ARPONG_EVENT));
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBReceiver);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBReceiver, new IntentFilter(ArpongEngine.ARPONG_EVENT));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBReceiver);
    }

    private BroadcastReceiver mBReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int type = intent.getIntExtra(ArpongEngine.ARPONG_EVENT_TYPE, -1);
            Log.i(TAG, "GOT INTENT .. type: " + type + " message " + intent.toString());

            switch(type) {
                case ArpongEngine.APRONG_EVENT_SQUARE_ON: {
                    Log.i(TAG, "got sq on intent!");

                    int id = intent.getIntExtra(ArpongEngine.ARPONG_SQUARE_ON_ID, -1);
                    int degree = intent.getIntExtra(ArpongEngine.ARPONG_SQUARE_ON_DEGREE, -1);
                    int step = intent.getIntExtra(ArpongEngine.ARPONG_SQUARE_ON_STEP, -1);
                    double velocity = intent.getDoubleExtra(ArpongEngine.ARPONG_SQUARE_ON_VEL, 0.1);


                    String myCommand = String.format("javascript:matrix[%d][%d].bang(%f)",degree, step, velocity);
                    WebView w = (WebView) findViewById(R.id.web);
                    w.addJavascriptInterface(new JsInterface(), "AndroidApp");
                    w.loadUrl("javascript:changeBackgroundColor()");
                    //w.loadUrl("javascript:matrix[5][5].bang(2)");
                    w.loadUrl(myCommand);


//                    myCommand = String.format("javascript:sequence_active(%d)", step);
//                    w.loadUrl(myCommand);

                    //funky
                    if ((degree + step) % 7 == 0) {
                        myCommand = String.format("javascript:matrix[%d][%d].bang_collision(%f)",degree, step, velocity);
                        w.loadUrl(myCommand);
                    }

                }
                break;
                case ArpongEngine.APRONG_EVENT_STEP: {
                    int step = intent.getIntExtra(ArpongEngine.ARPONG_SQUARE_ON_STEP, -1);

                    WebView w = (WebView) findViewById(R.id.web);
                    w.addJavascriptInterface(new JsInterface(), "AndroidApp");
                    w.loadUrl("javascript:changeBackgroundColor()");

                    String myCommand;
//                    if (step == 0) {
//                        myCommand = String.format("reset()");
//                        w.loadUrl(myCommand);
//                    }

                    myCommand = String.format("javascript:sequence_active(%d)", step);
                    w.loadUrl(myCommand);
                }
            }
        }
    };

    public void send(View view){
        WebView w = (WebView) findViewById(R.id.web);
        w.addJavascriptInterface(new JsInterface(), "AndroidApp");
        w.loadUrl("javascript:changeBackgroundColor()");
        //w.loadUrl("javascript:reset()");

        w.loadUrl("javascript:matrix[5][5].bang(2)");


    }

}
