package com.chainway.intentoutput;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "IntentOutput";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle the initial intent when activity is first created
        displayScanResult(getIntent(), "via Activity (onCreate)");

        // Register for the intent sent by InfoWedge
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction("com.infowedge.data");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(myBroadcastReceiver, filter);
        }

        // Register for broadcasts from ScanDataService
        IntentFilter serviceFilter = new IntentFilter();
        serviceFilter.addAction(ScanDataService.ACTION_DISPLAY_SCAN_DATA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(serviceDataReceiver, serviceFilter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(serviceDataReceiver, serviceFilter);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle new intent when activity is reused (due to singleTop launch mode)
        setIntent(intent);  // Update the current intent
        displayScanResult(intent, "via Activity (onNewIntent)");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(serviceDataReceiver);
    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();

            // output the contents of the intent to the log
            for (String key : bundle.keySet()) {
                Log.v(TAG, key + " : " + bundle.get(key));
            }

            if (action.equals("com.infowedge.data")) {
                //  Received a barcode scan
                try {
                    // Update the text view on the screen with the data received
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };

    // Receiver for data forwarded from ScanDataService
    private BroadcastReceiver serviceDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            
            if (ScanDataService.ACTION_DISPLAY_SCAN_DATA.equals(action)) {
                Log.d(TAG, "Received scan data from ScanDataService");
                
                // Get the delivery method indicator
                String receivedVia = intent.getStringExtra("received_via");
                
                try {
                    // Display the scan result on UI
                    displayScanResult(intent, "via Service (forwarded to Activity)");
                } catch (Exception e) {
                    Log.e(TAG, "Error displaying scan result from service", e);
                }
            }
        }
    };

    private void displayScanResult(Intent intent, String howDataReceived) {
        if (intent == null) {
            return;
        }
        
        // Check if this is a valid scan data intent
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        
        // Accept data from InfoWedge directly or forwarded from ScanDataService
        if (!action.equals("com.infowedge.data") && 
            !action.equals(ScanDataService.ACTION_DISPLAY_SCAN_DATA)) {
            return;
        }

        final TextView lblScanData = (TextView) findViewById(R.id.lblScanData);
        StringBuilder sb = new StringBuilder();

        // How the data was received
        sb.append("[how data received]\n").append(howDataReceived).append("\n\n");

        // Extract the result of the scan
        int result = intent.getIntExtra("result", -1);
        switch (result) {
            case 1:
                sb.append("[result]\n").append("success").append("\n\n");
                break;
            case -1:
                sb.append("[result]\n").append("cancel").append("\n\n");
                break;
            default:
                sb.append("[result]\n").append("failure").append("\n\n");
                break;
        }

        if (result != 1) {
            // Update the text view on the screen with the data received
            lblScanData.setText(sb.toString());
            return;
        }

        // Extract the data of the scan result
        sb.append("[scan data]\n").append(intent.getStringExtra("data_string")).append("\n\n");

        // Extract the raw data from the intent
        byte[] decode_data = intent.getByteArrayExtra("decode_data");
        if (decode_data != null) {
            sb.append("[raw data]\n").append(new String(decode_data)).append("\n\n");
        }

        // Extract the symbology of the barcode
        int symbol = intent.getIntExtra("symbology", -1);
        if (symbol == -1) {
            sb.append("[symbology]\nunknown\n\n");
        } else {
            sb.append("[symbology]\n").append(BarcodeSymbol.getSymbolName(symbol))
                    .append(" (").append(symbol).append(")").append("\n\n");
        }

        // Extract the time it took to decode the barcode
        sb.append("[decode time]\n").append(intent.getIntExtra("decode_time", -1)).append(" ms\n\n");

        // Update the text view on the screen with the data received
        lblScanData.setText(sb.toString());
    }
}