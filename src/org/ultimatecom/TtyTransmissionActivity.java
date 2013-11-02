package org.ultimatecom;

import java.io.IOException;

import android.os.Bundle;


public class TtyTransmissionActivity extends TransmissionActivity {

    @Override
    protected void writeDataToDevice(byte[] data) throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
