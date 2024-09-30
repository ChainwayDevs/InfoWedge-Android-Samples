# Intent 输出

> 本示例演示了应用程序如何通过 Android Intent 接收扫码结果

[![language](https://img.shields.io/badge/en-English-green.svg)](README.md)

## 演示环境

- InfoWedge - v1.46
- 设备 - MC62

## 概述

本示例演示了应用程序如何通过 Android Intent 来接收扫码结果。操作步骤如下：

1. 配置 InfoWedge 的 Profile0 ，打开 Intent 输出功能。

<img src="./pics/1.png" alt="1.png" width="180" height="300" style="border: 1px solid gray;">
<img src="./pics/2.png" alt="2.png" width="180" height="300" style="border: 1px solid gray;">

2. 运行示例应用程序，扫描条码。应用程序将显示以下信息：
   - 扫码结果
   - 条码数据
   - 原始数据
   - 条码类型
   - 扫码时长

<img src="./pics/3.png" alt="3.png" width="180" height="300" style="border: 1px solid gray;">
<img src="./pics/4.png" alt="4.png" width="180" height="300" style="border: 1px solid gray;">

## 示例代码说明

1. **注册广播接收器。** 由于 InfoWedge 配置为发送广播 intent，因此应用程序必须注册广播接收器。这是在示例应用程序的 onCreate（） 方法中完成的：
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
2. **定义广播接收器。** 这是在示例应用程序中以 MainActivity.java 完成的：
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
3. **提取扫描的数据并将其显示在屏幕上。** 这是在示例应用程序的 displayScanResult（） 方法中完成的：
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
