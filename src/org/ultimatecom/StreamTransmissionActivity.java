package org.ultimatecom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Bundle;
import android.util.Log;

public abstract class StreamTransmissionActivity extends TransmissionActivity {

    private Thread readThread = null;
    private IStreamTransmissionDevice streamTransmissionDevice = null;
    
    protected static interface IStreamTransmissionDevice {
        public InputStream getInputStream();
        public OutputStream getOutputStream();
        public void close() throws IOException;
    }
    
    protected abstract IStreamTransmissionDevice openDevice() throws IOException;

    @Override
    protected void writeDataToDevice(byte[] data) throws IOException {
        streamTransmissionDevice.getOutputStream().write(data);
        streamTransmissionDevice.getOutputStream().flush();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            streamTransmissionDevice = openDevice();
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), "Could not open device", e);
        }
        
        readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buffer = new byte[10];
                try {
                    while (!Thread.interrupted()) {
                        waitNotPaused();
                        if (streamTransmissionDevice.getInputStream().available() > 0) {
                            int readBytesCount = streamTransmissionDevice.getInputStream().read(buffer);
                            if (readBytesCount > 0) {
                                showReadData(buffer, readBytesCount);
                            }
                        } else {
                            Thread.sleep(50);
                        }
                    }
                } catch (InterruptedException e) {
                } catch (Exception e) {
                }
            }
        });
        readThread.setDaemon(true);
        readThread.start();
    }

    @Override
    protected void onDestroy() {
        if (readThread != null) {
            readThread.interrupt();
            try {
                readThread.join();
            } catch (InterruptedException e) {
            }
        }
        if (streamTransmissionDevice != null) {
            try {
                streamTransmissionDevice.close();
            } catch (IOException e) {
            }
        }
        super.onDestroy();
    }

}
