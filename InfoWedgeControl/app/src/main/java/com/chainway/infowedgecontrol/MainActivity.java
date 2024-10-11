package com.chainway.infowedgecontrol;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "InfoWedgeControl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable InfoWedge
        Button btnEnableInfoWedge = findViewById(R.id.button_enable_iw);
        btnEnableInfoWedge.setOnClickListener(v -> {
            enableInfoWedge(true);
        });

        // Disable InfoWedge
        Button btnDisableInfoWedge = findViewById(R.id.button_disable_iw);
        btnDisableInfoWedge.setOnClickListener(v -> {
            enableInfoWedge(false);
        });

        // Get the status of InfoWedge
        Button btnGetStatus = findViewById(R.id.button_get_iw_status);
        btnGetStatus.setOnClickListener(v -> {
            getStatus();
        });

        // Toggle the scan
        Button btnToggleScan = findViewById(R.id.button_scan);
        btnToggleScan.setOnClickListener(v -> {
            toggleScan();
        });

        // Register broadcast receiver and filter results
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
        filter.addCategory("android.intent.category.DEFAULT");
        registerReceiver(resultBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        // Unregister the broadcast receiver
        unregisterReceiver(resultBroadcastReceiver);

        super.onDestroy();
    }

    private void getStatus() {
        // Get the status of InfoWedge (enable or disable)
        Intent i = new Intent();
        i.setAction("com.symbol.infowedge.api.ACTION");
        i.setPackage("com.rscja.infowedge");
        i.putExtra("com.symbol.infowedge.api.GET_INFOWEDGE_STATUS", "");
        sendBroadcast(i);
    }

    private void toggleScan() {
        // Toggle the scan
        Intent i = new Intent();
        i.setAction("com.symbol.infowedge.api.ACTION");
        i.putExtra("com.symbol.infowedge.api.SOFT_TRIGGER", "TOGGLE"); // START, STOP, TOGGLE
        i.putExtra("SEND_RESULT", "true");
        i.putExtra("COMMAND_IDENTIFIER", "SOFT_TRIGGER");
        sendBroadcast(i);
    }

    private void enableInfoWedge(boolean enable) {
        // Enable or disable InfoWedge
        Intent i = new Intent();
        i.setAction("com.symbol.infowedge.api.ACTION");
        i.putExtra("com.symbol.infowedge.api.ENABLE_INFOWEDGE", enable);
        i.putExtra("SEND_RESULT", "true");
        i.putExtra("COMMAND_IDENTIFIER", "ENABLE_INFOWEDGE");
        sendBroadcast(i);
    }

    private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder();

            // The status of InfoWedge
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS")) {
                // status: "ENABLED" or "DISABLED"
                String infoWedgeStatus = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS");
                sb.append("INFO WEDGE STATUS: " + infoWedgeStatus + "\n");
            }

            // The result of the command
            if (intent.hasExtra("COMMAND")) {
                String command = intent.getStringExtra("COMMAND");
                sb.append("COMMAND: " + command + "\n");
            }
            if (intent.hasExtra("COMMAND_IDENTIFIER")) {
                String commandId = intent.getStringExtra("COMMAND_IDENTIFIER");
                sb.append("COMMAND ID: " + commandId + "\n");
            }
            if (intent.hasExtra("RESULT")) {
                String result = intent.getStringExtra("RESULT");
                sb.append("RESULT: " + result + "\n");
            }
            if (intent.hasExtra("RESULT_INFO")) {
                sb.append("+ RESULT INFO:\n");
                Bundle bundle = intent.getBundleExtra("RESULT_INFO");
                for (String key : bundle.keySet()) {
                    sb.append("\t- " + key + ": " + bundle.get(key) + "\n");
                }
            }

            // display the result on the screen
            if (sb.length() > 0) {
                ((TextView) findViewById(R.id.label_result)).setText(sb);
            }
        }
    };
}
