# InfoWedge API 使用入门

> 此示例应用程序演示了使用 InfoWedge API 的入门编程。

[![language](https://img.shields.io/badge/en-English-green.svg)](README.md)

## 演示环境

- InfoWedge - v1.48
- 设备 - MC62

## 概述
此示例应用程序演示了如何开始使用 InfoWedge API 进行编程。运行示例应用程序并点击 `GET INFO` 按钮以获取有关 InfoWedge 的一些信息。

![1.png](./pics/1.png) ![overview.png](./pics/overview.png)

## 示例代码说明

1. **注册广播接收器并过滤结果。** 这是在示例应用程序的 `onCreate()` 方法中完成的：
    ```java
    // Register broadcast receiver and filter results
    IntentFilter filter = new IntentFilter();
    filter.addAction("com.symbol.infowedge.api.RESULT_ACTION");
    filter.addCategory("android.intent.category.DEFAULT");
    registerReceiver(resultBroadcastReceiver, filter);
    ```
2. **处理按钮点击事件。** 这是在示例应用程序的 `onCreate()` 方法中完成的：
    ```java
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
    ```
3. **定义广播接收器。** 在 `resultBroadcastReceiver` 中获取有关 InfoWedge 的信息并显示在屏幕上。这是在示例应用程序的 MainActivity.java 中完成的：
    ```java
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
    ```