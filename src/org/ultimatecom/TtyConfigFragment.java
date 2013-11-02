package org.ultimatecom;

import org.ultimatecom.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;

public class TtyConfigFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_tty_config, container, false);
        
        final Button connectButton = (Button) view.findViewById(R.id.connect_button);
        connectButton.setEnabled(false);
        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TtyTransmissionActivity.class));
            }
        });
        
        final AutoCompleteTextView ttyPathTextView = (AutoCompleteTextView) view.findViewById(R.id.tty_path_text_view);
        ttyPathTextView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[] { "/dev/ttyUSB0", "/dev/ttyUSB1" }));
        ttyPathTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                connectButton.setEnabled(ttyPathTextView.getText().length() > 0);
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                connectButton.setEnabled(ttyPathTextView.getText().length() > 0);
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                connectButton.setEnabled(ttyPathTextView.getText().length() > 0);
            }
        });
        
        final Spinner baudRateSpinner = (Spinner) view.findViewById(R.id.baud_rate_spinner);
        baudRateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new String[] { "19200", "38400" }));
        
        return view;
    }

}
