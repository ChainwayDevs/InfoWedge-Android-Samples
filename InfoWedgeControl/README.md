# InfoWedge Control

> This sample application demonstrates how to use the InfoWedge APIs to control the barcode scanner on Chainway devices.

[![language](https://img.shields.io/badge/cn-简体中文-green.svg)](README_zh-CN.md)

## Demonstration Environment

- InfoWedge - v1.48
- Device - MC62

## Overview

This sample application demonstrates how to use the InfoWedge APIs to control the barcode scanner on Chainway devices.

1. Run the sample application and click the `ENABLE IW` or `DISABLE IW` button to enable or disable the InfoWedge service.
2. Click the `GET IW STATUS` button to get the enabled status of the InfoWedge.
3. Click the `SCAN` button to start or stop scanning.
4. The result of the command will be displayed under the buttons.

![overview.png](./pics/overview.png)

## Sample code walk-through

1. **Register broadcast receiver and filter results.** This is done in the `onCreate()` method of the sample application:
    ```java
    // Register broadcast receiver and filter results
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
    filter.addCategory("android.intent.category.DEFAULT");
    registerReceiver(resultBroadcastReceiver, filter);
    ```
2. **Process the button click event.** This is done in the `onCreate()` method of the sample application:
    ```java
    // Enable InfoWedge
    Button btnEnableInfoWedge = findViewById(R.id.button_enable_iw);
    btnEnableInfoWedge.setOnClickListener(v -> {
        enableInfoWedge(true);
    });

    // Disable InfoWedge ...

    // Get the status of InfoWedge ...

    // Toggle the scan
    Button btnToggleScan = findViewById(R.id.button_scan);
    btnToggleScan.setOnClickListener(v -> {
        toggleScan();
    });
    ```
3. **Define the button click handlers.** This is done in the sample application in MainActivity.java:
    ```
    private void enableInfoWedge(boolean enable) {
        // Enable or disable InfoWedge
        Intent i = new Intent();
        i.setAction("com.symbol.infowedge.api.ACTION");
        i.putExtra("com.symbol.infowedge.api.ENABLE_INFOWEDGE", enable);
        i.putExtra("SEND_RESULT", "true");
        i.putExtra("COMMAND_IDENTIFIER", "ENABLE_INFOWEDGE");
        sendBroadcast(i);
    }
    // some other methods ...
    ```
4. **Define the broadcast receiver.** Get the result of the command in the `resultBroadcastReceiver` and display it on the screen. This is done in the sample application in MainActivity.java:
    ```java
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
    ```