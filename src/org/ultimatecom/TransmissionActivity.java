package org.ultimatecom;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ultimatecom.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public abstract class TransmissionActivity extends Activity {

    private static final String INSTANCE_STATE_KEY_READ_DATA = "INSTANCE_STATE_KEY_READ_DATA";
    private static final String INSTANCE_STATE_KEY_WRITTEN_DATA = "INSTANCE_STATE_KEY_WRITTEN_DATA";

    private final AtomicBoolean isPaused = new AtomicBoolean(false);
    private TextView readDataTextView;
    private ScrollView readDataScrollView;
    private RingBufferCharSequence readDataCharSequence;
    private RingBufferCharSequence writtenDataCharSequence;
    
    private final Runnable refreshReadDataTextView = new Runnable() {
        @Override
        public void run() {
            refreshDataTextView(readDataCharSequence, readDataTextView, readDataScrollView);
        }
    };
    
    protected abstract void writeDataToDevice(byte[] data) throws IOException;
    
    private final void refreshDataTextView(CharSequence charSequence, TextView textView, ScrollView scrollView) {
        textView.setText(charSequence);
        textView.invalidate();
        textView.requestLayout();
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }
    
    protected final void showReadData(byte[] data, int length) {
        readDataCharSequence.append(data, length);
        runOnUiThread(refreshReadDataTextView);
    }
    
    protected void waitNotPaused() throws InterruptedException {
        synchronized (isPaused) {
            while (isPaused.get()) {
                isPaused.wait();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmission);
        
        if (savedInstanceState == null) {
            readDataCharSequence = new RingBufferCharSequence(16 * 1024, Charset.defaultCharset());
            writtenDataCharSequence = new RingBufferCharSequence(16 * 1024, Charset.defaultCharset());
        } else {
            readDataCharSequence = (RingBufferCharSequence) savedInstanceState.getSerializable(INSTANCE_STATE_KEY_READ_DATA);
            writtenDataCharSequence = (RingBufferCharSequence) savedInstanceState.getSerializable(INSTANCE_STATE_KEY_WRITTEN_DATA);
        }
        
        readDataTextView = (TextView) this.findViewById(R.id.read_data_textview);
        readDataScrollView = (ScrollView) this.findViewById(R.id.read_data_scrollview);
        
        final TextView writtenDataTextView = (TextView) this.findViewById(R.id.written_data_textview);
        final ScrollView writtenDataScrollView = (ScrollView) this.findViewById(R.id.written_data_scrollview);
        
        final EditText dataToSendEditText = (EditText) this.findViewById(R.id.data_to_send_edittext);
        
        final Button sendButton = (Button) this.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CharBuffer dataCharBuffer = CharBuffer.wrap(dataToSendEditText.getText());
                byte[] inputDataArray = writtenDataCharSequence.getCurrentCharset().encode(dataCharBuffer).array();
                byte[] arrayToSend = new byte[inputDataArray.length + 1];
                System.arraycopy(inputDataArray, 0, arrayToSend, 0, inputDataArray.length);
                arrayToSend[arrayToSend.length - 1] = (byte) '\n';
                try {
                    writeDataToDevice(arrayToSend);
                } catch (IOException e) {
                    // TODO: Show error!
                    return;
                }
                writtenDataCharSequence.append(arrayToSend, arrayToSend.length);
                refreshDataTextView(writtenDataCharSequence, writtenDataTextView, writtenDataScrollView);
                dataToSendEditText.setText("");
            }
        });
        
        if (savedInstanceState != null) {
            refreshDataTextView(readDataCharSequence, readDataTextView, readDataScrollView);
            refreshDataTextView(writtenDataCharSequence, writtenDataTextView, writtenDataScrollView);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(INSTANCE_STATE_KEY_READ_DATA, readDataCharSequence);
        outState.putSerializable(INSTANCE_STATE_KEY_WRITTEN_DATA, writtenDataCharSequence);
    }

    @Override
    protected void onPause() {
        super.onPause();
        synchronized (isPaused) {
            isPaused.set(true);
            isPaused.notifyAll();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        synchronized (isPaused) {
            isPaused.set(false);
            isPaused.notifyAll();
        }
    }

}
