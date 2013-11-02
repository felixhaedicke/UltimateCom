package org.ultimatecom;

import org.ultimatecom.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

public class ConnectionConfigActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_config);

        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        tabHost.addTab(tabHost.newTabSpec("tty").setIndicator("TTY"),
                TtyConfigFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("usbserial").setIndicator("USB"),
                UsbSerialConfigFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("ioi").setIndicator("IOIO"),
                IoioConfigFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("bluetooth").setIndicator("BT"),
                BluetoothConfigFragment.class, null);
        tabHost.addTab(tabHost.newTabSpec("socket").setIndicator("TCP"),
                TcpConfigFragment.class, null);
    }

}
