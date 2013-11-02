package org.ultimatecom;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.ultimatecom.R;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class BluetoothConfigFragment extends Fragment {

    private Spinner btDeviceSpinner;
    private Button connectButton;
    private Timer refreshDevicesListTimer;

    private static final class BluetoothDeviceDescriptor {
        final String displayName;
        final String address;
        
        public BluetoothDeviceDescriptor(String displayName, String address) {
            this.displayName = displayName;
            this.address = address;
        }
        
        public final String getAddress() {
            return address;
        }
        
        @Override
        public String toString() {
            return ((displayName == null) || (displayName.length() == 0)) ? address : displayName;
        }
    }
    
    private Runnable refreshDevicesListRunnable = new Runnable() {
        @Override
        public void run() {
            refreshDevicesList();
        }
    };
    
    private final void refreshDevicesList() {
        boolean foundDevices = false;
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null) {
            Set<BluetoothDevice> btDevices = btAdapter.getBondedDevices();
            if ((btDevices != null) && (btDevices.size() > 0)) {
                BluetoothDeviceDescriptor[] btDeviceDescriptors = new BluetoothDeviceDescriptor[btDevices.size()];
                {
                    int i = 0;
                    for (BluetoothDevice btDevice : btDevices) {
                        btDeviceDescriptors[i] = new BluetoothDeviceDescriptor(btDevice.getName(), btDevice.getAddress());
                        ++i;
                    }
                }
                foundDevices = true;
                btDeviceSpinner.setAdapter(new ArrayAdapter<BluetoothDeviceDescriptor>(getActivity(), android.R.layout.simple_spinner_dropdown_item, btDeviceDescriptors));
            }
        }
        
        if (!foundDevices) {
            btDeviceSpinner.setAdapter(null);
        }
        connectButton.setEnabled(foundDevices);
    }
    
    private void startRefreshDevicesListTimer() {
        if (refreshDevicesListTimer == null) {
            TimerTask refreshDevicesListTimerTask = new TimerTask() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(refreshDevicesListRunnable);
                }
            };
            refreshDevicesListTimer = new Timer(true);
            refreshDevicesListTimer.schedule(refreshDevicesListTimerTask, 1000, 1000);
        }
    }
    
    private void stopRefreshDevicesListTimer() {
        if (refreshDevicesListTimer != null) {
            try {
                refreshDevicesListTimer.cancel();
            } catch (Exception e) {
                // TODO: Log!
            }
            refreshDevicesListTimer = null;
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bluetooth_config, container, false);
        
        btDeviceSpinner = (Spinner) view.findViewById(R.id.bluetooth_device_spinner);
        connectButton = (Button) view.findViewById(R.id.connect_button);
        
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BluetoothTransmissionActivity.class);
                intent.putExtra(BluetoothTransmissionActivity.PARAM_BT_ADDRESS_KEY, ((BluetoothDeviceDescriptor) btDeviceSpinner.getSelectedItem()).getAddress());
                startActivity(intent);
            }
        });
        connectButton.setEnabled(false);
        
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startRefreshDevicesListTimer();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        startRefreshDevicesListTimer();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        stopRefreshDevicesListTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRefreshDevicesListTimer();
    }

}
