# Intent Output

> This example demonstrates how an application receives scan results via Android Intent.

[![language](https://img.shields.io/badge/cn-简体中文-green.svg)](README_zh-CN.md)

## Demonstration Environment

- InfoWedge - v1.46
- Device - MC62

## Overview

This example demonstrates how an application receives scan results via Android Intent. The steps are as follows:

1. Configure InfoWedge's Profile0 and enable the Intent output feature.

<img src="./pics/1.png" alt="1.png" width="180" height="300" style="border: 1px solid gray;">
<img src="./pics/2.png" alt="2.png" width="180" height="300" style="border: 1px solid gray;">

2. Run the sample application and scan a barcode. The application will display the following information:
- Scan result
- Barcode data
- Raw data
- Barcode symbology
- Decode time

<img src="./pics/3.png" alt="3.png" width="180" height="300" style="border: 1px solid gray;">
<img src="./pics/overview.png" alt="overview.png" width="180" height="300" style="border: 1px solid gray;">

## Sample code walk-through

1. **Register the broadcast receiver.** Since InfoWedge is configured to send broadcast intents, the application must register a broadcast receiver. This is done in the onCreate() method of the sample application:
    ```java
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Register for the intent sent by InfoWedge
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction("com.infowedge.data");
        registerReceiver(myBroadcastReceiver, filter);
    }
    ```
2. **Define the broadcast receiver.** This is done in the sample application in MainActivity.java:
    ```java
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
                    displayScanResult(intent);
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast
                }
            }
        }
    };
    ```
3. **Extract the scanned data and display it on the screen.** This is done in the displayScanResult() method of the sample application:
    ```java
    private void displayScanResult(Intent intent) {
        final TextView lblScanData = (TextView) findViewById(R.id.lblScanData);
        StringBuilder sb = new StringBuilder();

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

        // Extract the source of the data
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
    ```
