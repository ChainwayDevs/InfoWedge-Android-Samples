package com.chainway.helloinfowedge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HelloInfoWedge";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGetInfo = findViewById(R.id.button_get_info);
        btnGetInfo.setOnClickListener(v -> {
            // Send multiple intents as extras to get different information
            Intent i = new Intent();
            i.setAction("com.symbol.infowedge.api.ACTION");

            // Gets the version number of InfoWedge currently installed on the device
            i.putExtra("com.symbol.infowedge.api.GET_VERSION_INFO", "");

            // Gets the status of InfoWedge as "enabled" or "disabled" as a string extra
            i.putExtra("com.symbol.infowedge.api.GET_INFOWEDGE_STATUS", "");

            // Gets the name of the Profile currently in use by InfoWedge
            i.putExtra("com.symbol.infowedge.api.GET_ACTIVE_PROFILE", "");

            sendBroadcast(i);
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

    private BroadcastReceiver resultBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            StringBuilder sb = new StringBuilder();

            // Version number of InfoWedge
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO")) {
                Bundle res = intent.getBundleExtra("com.symbol.infowedge.api.RESULT_GET_VERSION_INFO");
                String infoWedgeVersion = res.getString("INFOWEDGE");
                sb.append("version:\t").append(infoWedgeVersion).append("\n");
                Log.d(TAG, "version: " + infoWedgeVersion);
            }

            // The name of the Profile currently in use
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE")) {
                String activeProfile = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_ACTIVE_PROFILE");
                sb.append("profile:\t").append(activeProfile).append("\n");
                Log.d(TAG, "profile: " + activeProfile);
            }

            // The status of InfoWedge
            if (intent.hasExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS")) {
                // status: "ENABLED" or "DISABLED"
                String infoWedgeStatus = intent.getStringExtra("com.symbol.infowedge.api.RESULT_GET_INFOWEDGE_STATUS");
                sb.append("status:\t").append(infoWedgeStatus).append("\n");
                Log.d(TAG, "status: " + infoWedgeStatus);
            }

            ((TextView)findViewById(R.id.label_info_data)).setText(sb.toString());
        }
    };
}