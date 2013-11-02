package org.ultimatecom;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothTransmissionActivity extends StreamTransmissionActivity {

    public static final String PARAM_BT_ADDRESS_KEY = "PARAM_BT_ADDRESS_KEY";

    private static final class BluetoothStreamTransmissionDevice implements IStreamTransmissionDevice {
        private final BluetoothSocket btSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;
        
        public BluetoothStreamTransmissionDevice(BluetoothSocket btSocket) throws IOException {
            this.btSocket = btSocket;
            inputStream = btSocket.getInputStream();
            outputStream = btSocket.getOutputStream();
        }
        
        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStream;
        }

        @Override
        public void close() throws IOException {
            btSocket.close();
        }
    }

    @Override
    protected IStreamTransmissionDevice openDevice() throws IOException {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice btDevice = btAdapter.getRemoteDevice(getIntent().getStringExtra(PARAM_BT_ADDRESS_KEY));
        
        BluetoothSocket btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        btSocket.connect();
        
        String deviceName = btDevice.getName();
        if ((deviceName == null) || (deviceName.length() == 0)) {
            deviceName = btDevice.getAddress();
        }
        this.setTitle("BT Device: " + deviceName);
        
        return new BluetoothStreamTransmissionDevice(btSocket);
    }

}
